package com.revify.monolith.user.service.util;

import lombok.SneakyThrows;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class UserMailKeyGenerator {
    private final static String CIPHER_KEY_ = "AIOSDJioasjdonsad.o0k1039dMSAIDK90j9213iwkemd.";

    @SneakyThrows
    public static String generateKey() {
        return new String(getKeyFromKeyGenerator().getEncoded());
    }

    private static SecretKey getKeyFromKeyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_KEY_);
        keyGenerator.init(6);
        return keyGenerator.generateKey();
    }
}
