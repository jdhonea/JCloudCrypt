package cloudcrypt;

import java.io.File;
import java.security.SecureRandom;
import static com.kosprov.jargon2.api.Jargon2.*;
import java.util.Scanner;

public class encrypt extends pathReader {
    private String password;

    public encrypt() {
        /*
         * this.password = passwordHash(); decrypt decrypt = new decrypt(this.password);
         */
    }

    private void encryptFile() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        byte[] key = new byte[16];
        secureRandom.nextBytes(iv);
    }

    private void encryptDirectory() {

    }

    public String passwordHash() {
        String encodedHash = "";
        try {
            Scanner in = new Scanner(System.in);
            String password = "";
            do {
                System.out.println("Enter a password");
                password = in.nextLine();
            } while (password.length() <= 8);
            byte[] passBytes = password.getBytes();
            Hasher hasher = jargon2Hasher().type(Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536) // 64MB memory cost
                    .timeCost(3) // 3 passes through memory
                    .parallelism(4) // use 4 lanes and 4 threads
                    .saltLength(16) // 16 random bytes salt
                    .hashLength(16); // 16 bytes output hash
            encodedHash = hasher.password(passBytes).encodedHash();
        } catch (Exception e) {
            System.out.println("Password scanner exception caught.");
        }
        return encodedHash;
    }
}