package cloudcrypt;

import java.util.Scanner;
import static com.kosprov.jargon2.api.Jargon2.*;

public class decrypt extends pathReader {
    public decrypt(String encodedHash) {
        boolean matches = false;
        Scanner in = new Scanner(System.in);
        String password = "";
        Verifier verifier = jargon2Verifier();
        do {
            System.out.println("Enter a password");
            password = in.nextLine();
            byte[] passBytes = password.getBytes();
            matches = verifier.hash(encodedHash).password(passBytes).verifyEncoded();
            if (!matches) {
                System.out.println("Password is incorrect.");
            }
        } while (password.length() <= 8 || !matches);
        // in.close();
        System.out.printf("Matches: %s%n", matches);
    }
}