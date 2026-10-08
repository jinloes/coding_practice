package com.jinloes.cryptography.envelope;

import com.jinloes.cryptography.kms.DataKey;
import java.security.GeneralSecurityException;
import java.util.Map;

/** Supplies data keys for envelope encryption, either straight from KMS or from a cache. */
@FunctionalInterface
public interface DataKeySource {
    DataKey dataKey(String keyId, Map<String, String> encryptionContext) throws GeneralSecurityException;
}
