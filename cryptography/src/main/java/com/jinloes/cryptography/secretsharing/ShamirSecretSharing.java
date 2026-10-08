package com.jinloes.cryptography.secretsharing;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shamir's k-of-n secret sharing over GF(2^8), applied independently to each byte of the secret.
 *
 * <p>Used to split a root/unseal key among operators so no single person can unseal a secrets
 * manager or HSM, while losing a few shares is survivable. Any k shares reconstruct the secret;
 * k - 1 shares reveal nothing about it (information-theoretic security).
 */
public final class ShamirSecretSharing {
    private static final SecureRandom RANDOM = new SecureRandom();

    /** One share: the x coordinate (1..255) and the polynomial values at x for every secret byte. */
    public record Share(int x, byte[] y) {
    }

    private ShamirSecretSharing() {
    }

    public static List<Share> split(byte[] secret, int shares, int threshold) {
        if (threshold < 2 || threshold > shares || shares > 255) {
            throw new IllegalArgumentException("require 2 <= threshold <= shares <= 255");
        }
        byte[][] ys = new byte[shares][secret.length];
        int[] coefficients = new int[threshold];
        for (int i = 0; i < secret.length; i++) {
            // f(x) = secret[i] + c1*x + ... + c(k-1)*x^(k-1); f(0) is the secret byte.
            coefficients[0] = secret[i] & 0xff;
            for (int c = 1; c < threshold; c++) {
                coefficients[c] = RANDOM.nextInt(256);
            }
            for (int s = 0; s < shares; s++) {
                ys[s][i] = (byte) evaluate(coefficients, s + 1);
            }
        }
        List<Share> result = new ArrayList<>(shares);
        for (int s = 0; s < shares; s++) {
            result.add(new Share(s + 1, ys[s]));
        }
        return result;
    }

    /**
     * Lagrange interpolation at x = 0. With fewer than threshold shares this silently returns garbage,
     * which is why production schemes also store a MAC or hash of the secret to detect it.
     */
    public static byte[] combine(List<Share> shares) {
        if (shares.isEmpty()) {
            throw new IllegalArgumentException("no shares");
        }
        int length = shares.get(0).y().length;
        Set<Integer> xs = new HashSet<>();
        for (Share share : shares) {
            if (share.y().length != length || share.x() < 1 || share.x() > 255 || !xs.add(share.x())) {
                throw new IllegalArgumentException("shares must be distinct, valid, and equal length");
            }
        }
        byte[] secret = new byte[length];
        for (int i = 0; i < length; i++) {
            int value = 0;
            for (Share si : shares) {
                int basis = 1;
                for (Share sj : shares) {
                    if (si != sj) {
                        // (0 - xj) / (xi - xj); subtraction is XOR in GF(2^8).
                        basis = mul(basis, div(sj.x(), si.x() ^ sj.x()));
                    }
                }
                value ^= mul(si.y()[i] & 0xff, basis);
            }
            secret[i] = (byte) value;
        }
        return secret;
    }

    private static int evaluate(int[] coefficients, int x) {
        int result = 0;
        for (int c = coefficients.length - 1; c >= 0; c--) {
            result = mul(result, x) ^ coefficients[c];
        }
        return result;
    }

    /** Multiplication modulo the AES polynomial x^8 + x^4 + x^3 + x + 1. */
    static int mul(int a, int b) {
        int product = 0;
        while (b != 0) {
            if ((b & 1) != 0) {
                product ^= a;
            }
            a <<= 1;
            if ((a & 0x100) != 0) {
                a ^= 0x11b;
            }
            b >>= 1;
        }
        return product;
    }

    static int div(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("division by zero in GF(256)");
        }
        // b^254 = b^-1 because the multiplicative group has order 255.
        int inverse = 1;
        for (int i = 0; i < 254; i++) {
            inverse = mul(inverse, b);
        }
        return mul(a, inverse);
    }
}
