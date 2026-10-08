package com.jinloes.cryptography.kms;

import com.jinloes.cryptography.aead.AesGcm;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Single-process stand-in for a KMS backed by an HSM. Each key id maps to a key ring of versions.
 *
 * <p>Ciphertext blob format:
 * <pre>
 *   byte   formatVersion (1)
 *   short  keyIdLength, then keyId UTF-8 bytes
 *   int    keyVersion
 *   ...    AES-256-GCM output (nonce || ciphertext || tag)
 * </pre>
 * The header and the canonical encryption context are both bound in as associated data, so an
 * attacker cannot splice a blob onto a different key id, version, or context.
 */
public final class InMemoryKms implements KeyManagementService {
    private static final byte FORMAT_VERSION = 1;

    private final Map<String, KeyRing> keys = new ConcurrentHashMap<>();
    private final List<AuditEvent> auditLog = new CopyOnWriteArrayList<>();
    private final Clock clock;

    public InMemoryKms() {
        this(Clock.systemUTC());
    }

    public InMemoryKms(Clock clock) {
        this.clock = clock;
    }

    @Override
    public String createKey() {
        String keyId = UUID.randomUUID().toString();
        keys.put(keyId, new KeyRing());
        audit("CreateKey", keyId, 1, Map.of(), true);
        return keyId;
    }

    @Override
    public int rotateKey(String keyId) {
        int version = ring(keyId).rotate();
        audit("RotateKey", keyId, version, Map.of(), true);
        return version;
    }

    @Override
    public byte[] encrypt(String keyId, byte[] plaintext, Map<String, String> context)
            throws GeneralSecurityException {
        if (plaintext.length > MAX_PLAINTEXT_BYTES) {
            throw new IllegalArgumentException("plaintext exceeds " + MAX_PLAINTEXT_BYTES + " bytes; use envelope encryption");
        }
        KeyRing ring = ring(keyId);
        KeyRing.Version primary = ring.primary();
        byte[] header = header(keyId, primary.number());
        byte[] body = new AesGcm(primary.material()).encrypt(plaintext, associatedData(header, context));
        audit("Encrypt", keyId, primary.number(), context, true);
        return ByteBuffer.allocate(header.length + body.length).put(header).put(body).array();
    }

    @Override
    public byte[] decrypt(byte[] blob, Map<String, String> context) throws GeneralSecurityException {
        String keyId = "unknown";
        int versionNumber = -1;
        try {
            ByteBuffer in = ByteBuffer.wrap(blob);
            if (in.get() != FORMAT_VERSION) {
                throw new GeneralSecurityException("unsupported ciphertext format");
            }
            int keyIdLength = in.getShort();
            if (keyIdLength < 0 || keyIdLength > in.remaining()) {
                throw new GeneralSecurityException("malformed ciphertext");
            }
            byte[] keyIdBytes = new byte[keyIdLength];
            in.get(keyIdBytes);
            keyId = new String(keyIdBytes, StandardCharsets.UTF_8);
            versionNumber = in.getInt();
            byte[] header = Arrays.copyOf(blob, in.position());
            byte[] body = Arrays.copyOfRange(blob, in.position(), blob.length);

            KeyRing ring = keys.get(keyId);
            if (ring == null) {
                throw new GeneralSecurityException("decryption failed");
            }
            byte[] material = ring.usableMaterial(versionNumber);
            byte[] plaintext = new AesGcm(material).decrypt(body, associatedData(header, context));
            audit("Decrypt", keyId, versionNumber, context, true);
            return plaintext;
        } catch (BufferUnderflowException e) {
            audit("Decrypt", keyId, versionNumber, context, false);
            throw new GeneralSecurityException("malformed ciphertext");
        } catch (GeneralSecurityException e) {
            audit("Decrypt", keyId, versionNumber, context, false);
            throw e;
        }
    }

    @Override
    public DataKey generateDataKey(String keyId, Map<String, String> context) throws GeneralSecurityException {
        byte[] dek = AesGcm.generateKey();
        return new DataKey(dek, encrypt(keyId, dek, context));
    }

    public void disableVersion(String keyId, int version) {
        ring(keyId).setState(version, KeyState.DISABLED);
        audit("DisableKeyVersion", keyId, version, Map.of(), true);
    }

    public void enableVersion(String keyId, int version) {
        ring(keyId).setState(version, KeyState.ENABLED);
        audit("EnableKeyVersion", keyId, version, Map.of(), true);
    }

    /** Crypto-shredding: once destroyed, every ciphertext under this version is unrecoverable. */
    public void destroyVersion(String keyId, int version) {
        ring(keyId).setState(version, KeyState.DESTROYED);
        audit("DestroyKeyVersion", keyId, version, Map.of(), true);
    }

    public int primaryVersion(String keyId) {
        return ring(keyId).primary().number();
    }

    public KeyState state(String keyId, int version) {
        return ring(keyId).version(version).state();
    }

    public List<AuditEvent> auditLog() {
        return List.copyOf(auditLog);
    }

    private KeyRing ring(String keyId) {
        KeyRing ring = keys.get(keyId);
        if (ring == null) {
            throw new IllegalArgumentException("no such key: " + keyId);
        }
        return ring;
    }

    private void audit(String op, String keyId, int version, Map<String, String> context, boolean success) {
        auditLog.add(new AuditEvent(clock.instant(), op, keyId, version,
                context == null ? Map.of() : Map.copyOf(context), success));
    }

    private static byte[] header(String keyId, int version) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            byte[] id = keyId.getBytes(StandardCharsets.UTF_8);
            out.writeByte(FORMAT_VERSION);
            out.writeShort(id.length);
            out.write(id);
            out.writeInt(version);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bytes.toByteArray();
    }

    private static byte[] associatedData(byte[] header, Map<String, String> context) {
        byte[] ctx = EncryptionContext.canonicalize(context);
        return ByteBuffer.allocate(header.length + ctx.length).put(header).put(ctx).array();
    }

    /** All versions of one logical key. Synchronized because rotation and state changes are rare. */
    private static final class KeyRing {
        private final List<Version> versions = new ArrayList<>();
        private int primaryIndex;

        KeyRing() {
            rotate();
        }

        synchronized int rotate() {
            versions.add(new Version(versions.size() + 1, AesGcm.generateKey()));
            primaryIndex = versions.size() - 1;
            return versions.size();
        }

        synchronized Version primary() {
            Version primary = versions.get(primaryIndex);
            if (primary.state() != KeyState.ENABLED) {
                throw new IllegalStateException("primary version " + primary.number() + " is " + primary.state());
            }
            return primary;
        }

        synchronized Version version(int number) {
            if (number < 1 || number > versions.size()) {
                throw new IllegalArgumentException("no such version: " + number);
            }
            return versions.get(number - 1);
        }

        synchronized byte[] usableMaterial(int number) throws GeneralSecurityException {
            if (number < 1 || number > versions.size()) {
                throw new GeneralSecurityException("decryption failed");
            }
            Version version = versions.get(number - 1);
            if (version.state() != KeyState.ENABLED) {
                throw new GeneralSecurityException("key version " + number + " is " + version.state());
            }
            return version.material();
        }

        synchronized void setState(int number, KeyState next) {
            Version version = version(number);
            if (version.state() == KeyState.DESTROYED) {
                throw new IllegalStateException("destroyed versions cannot change state");
            }
            version.state = next;
            if (next == KeyState.DESTROYED) {
                Arrays.fill(version.material, (byte) 0);
            }
        }

        private static final class Version {
            private final int number;
            private final byte[] material;
            private KeyState state = KeyState.ENABLED;

            Version(int number, byte[] material) {
                this.number = number;
                this.material = material;
            }

            int number() {
                return number;
            }

            byte[] material() {
                return material;
            }

            KeyState state() {
                return state;
            }
        }
    }
}
