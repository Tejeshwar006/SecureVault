import java.util.Base64;

public class EncryptionUtil {
    private EncryptionUtil() {
    }

    public static String xorEncrypt(String plainText, String key) {
        if (plainText == null || plainText.isEmpty()) {
            return "";
        }
        char[] output = new char[plainText.length()];
        for (int i = 0; i < plainText.length(); i++) {
            output[i] = (char) (plainText.charAt(i) ^ key.charAt(i % key.length()));
        }
        return Base64.getEncoder().encodeToString(new String(output).getBytes());
    }

    public static String xorDecrypt(String cipherText, String key) {
        if (cipherText == null || cipherText.isEmpty()) {
            return "";
        }
        String decoded = new String(Base64.getDecoder().decode(cipherText));
        char[] output = new char[decoded.length()];
        for (int i = 0; i < decoded.length(); i++) {
            output[i] = (char) (decoded.charAt(i) ^ key.charAt(i % key.length()));
        }
        return new String(output);
    }
}
