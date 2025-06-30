package passwordmanager.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    public static String generateKey() { 
        byte[] key = new byte[16]; 
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
    
    public static String encrypt(String plainText, String base64Key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedText, String base64Key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}