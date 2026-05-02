package com.zentra.middleware.crypto.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Utilidad criptográfica para encriptar y desencriptar textos (ej. contraseñas de certificados)
 * usando AES-256 en modo GCM.
 */
public class AesEncryptionUtil {

    private static final Logger logger = Logger.getLogger(AesEncryptionUtil.class.getName());
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final String MASTER_KEY_ENV = "ZENTRA_MASTER_KEY";

    private static SecretKey getMasterKey() {
        String base64Key = System.getenv(MASTER_KEY_ENV);
        if (base64Key == null || base64Key.trim().isEmpty()) {
            // Clave temporal para desarrollo si no se provee en .env (32 bytes = 256 bits)
            logger.warning("ATENCION: ZENTRA_MASTER_KEY no encontrada en .env. Usando clave de desarrollo insegura.");
            base64Key = "MTEyMjMzNDQ1NTY2Nzc4ODk5MDBhYWJiY2NkZGVlZmY="; 
        }
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            new SecureRandom().nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getMasterKey(), parameterSpec);
            
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Error encriptando valor", e);
        }
    }

    public static String decrypt(String encryptedBase64) {
        if (encryptedBase64 == null) return null;
        try {
            byte[] cipherMessage = Base64.getDecoder().decode(encryptedBase64);
            
            ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, getMasterKey(), parameterSpec);
            
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error desencriptando valor", e);
        }
    }
}
