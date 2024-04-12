package com.devoxx.genie.service.security;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class SecurityKeyService {

    private static final String AES_ECB_PKCS_5_PADDING = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private final String key;

    SecurityKeyService(@Value("${security.key.secret}") String key) {
        this.key = key;
    }

    /**
     * Encrypt the key
     *
     * @param value The string value to be encrypted
     * @return The encrypted key
     * @throws EncryptionException If the encryption fails
     */
    public String encrypt(String value) throws EncryptionException {
        byte[] keyBytes = key.getBytes();

        try {
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS_5_PADDING);
            SecretKeySpec key = new SecretKeySpec(keyBytes, ALGORITHM);

            // The Encrypt Mode
            cipher.init(Cipher.ENCRYPT_MODE, key);

            //Encryption
            byte[] cipherText = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            //Encode Characters
            return Base64.encodeBase64URLSafeString(cipherText);
        } catch (Exception e) {
            throw new EncryptionException("Error while encrypting the value", e);
        }
    }

    public String decrypt(String value) throws EncryptionException {
        byte[] keyBytes = key.getBytes();
        try {
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS_5_PADDING);
            SecretKeySpec key = new SecretKeySpec(keyBytes, ALGORITHM);

            // The Decrypt Mode
            cipher.init(Cipher.DECRYPT_MODE, key);

            //Decode Characters
            byte[] plainText = cipher.doFinal(Base64.decodeBase64(value));

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Error while decrypting the value", e);
        }
    }
}
