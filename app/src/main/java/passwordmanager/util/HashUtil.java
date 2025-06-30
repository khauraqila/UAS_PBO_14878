package passwordmanager.util;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class HashUtil {
    public static String sha256(String input) {
        return Hashing.sha256()
                      .hashString(input, StandardCharsets.UTF_8)
                      .toString();
    }

    public static String generateRandomKey() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
