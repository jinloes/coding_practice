package com.jinloes.cryptography.kms;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        List<byte[]> encoded = new ArrayList<>(sorted.size() * 2);
        int totalBytes = Integer.BYTES;
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);
            byte[] value = entry.getValue().getBytes(StandardCharsets.UTF_8);
            encoded.add(key);
            encoded.add(value);
            totalBytes += 2 * Integer.BYTES + key.length + value.length;
        }
        ByteBuffer out = ByteBuffer.allocate(totalBytes).putInt(sorted.size());
        for (byte[] utf8 : encoded) {
            out.putInt(utf8.length).put(utf8);
        }
        return out.array();
    }
}
