package cloudcrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import static com.kosprov.jargon2.api.Jargon2.*;

public class decrypt extends pathReader {
    private String password;
    private String encodedHash;

    public void decryptFile() {
    }

    public boolean verifyPass(String encodedHash) {
        boolean matches = false;
        try {
            Verifier verifier = jargon2Verifier();
            do {
                byte[] passBytes = new byte[32];
                promptForPassword();
                MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Hashes password to 256 byte key
                passBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                matches = verifier.hash(encodedHash).password(passBytes).verifyEncoded();
                if (!matches) {
                    System.out.println("Password is incorrect.");
                }
            } while (!matches);
            System.out.printf("Matches: %s%n", matches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void promptForPassword() {
        try {
            Scanner in = new Scanner(System.in);
            String password = "";
            do {
                System.out.println("Enter the password");
                password = in.nextLine();
            } while (password.length() <= 8);
            this.password = password;
        } catch (Exception e) {
            System.out.println("Password scanner exception caught.");
        }
    }
}