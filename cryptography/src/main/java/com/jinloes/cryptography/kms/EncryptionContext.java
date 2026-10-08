package com.jinloes.cryptography.kms;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * Canonical, unambiguous serialization of an encryption context for use as AEAD associated data.
 *
 * <p>Keys are sorted so map iteration order cannot change the bytes, and every string is length
 * prefixed so {@code {"ab":"c"}} and {@code {"a":"bc"}} never collide.
 */
public final class EncryptionContext {
    private EncryptionContext() {
    }

    public static byte[] canonicalize(Map<String, String> context) {
        Map<String, String> sorted = new TreeMap<>(context == null ? Map.of() : context);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(sorted.size());
            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                writeString(out, entry.getKey());
                writeString(out, entry.getValue());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bytes.toByteArray();
    }

    private static void writeString(DataOutputStream out, String value) throws IOException {
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        out.writeInt(utf8.length);
        out.write(utf8);
    }
}
