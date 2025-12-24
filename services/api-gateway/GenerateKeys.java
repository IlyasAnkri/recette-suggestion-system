import java.security.*;
import java.util.Base64;

public class GenerateKeys {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        
        System.out.println("=== PRIVATE KEY (for User Profile Service - token signing) ===");
        System.out.println("-----BEGIN PRIVATE KEY-----");
        System.out.println(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pair.getPrivate().getEncoded()));
        System.out.println("-----END PRIVATE KEY-----");
        System.out.println();
        
        System.out.println("=== PUBLIC KEY (for API Gateway - token validation) ===");
        System.out.println("-----BEGIN PUBLIC KEY-----");
        System.out.println(Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pair.getPublic().getEncoded()));
        System.out.println("-----END PUBLIC KEY-----");
    }
}
