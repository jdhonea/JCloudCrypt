package cloudcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import static com.kosprov.jargon2.api.Jargon2.*;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class encrypt extends pathReader {

    public static void encryptFile() {
        String pathName = readPath();
        try {
            byte[] iv = new byte[16];
            byte[] saltPlain = new byte[32];
            byte[] saltPass = new byte[32];
            String password = promptForPassword();
            byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            secureRandom.nextBytes(saltPlain);
            secureRandom.nextBytes(saltPass);
            byte[] plainTextHash = passwordHash(passBytes, saltPlain);
            byte[] passwordHash = passwordHash(passBytes, saltPass);
            SecretKeySpec skey = new SecretKeySpec(passwordHash, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
            String outputPath = getPathName() + ".crypt";
            FileOutputStream fileOut = new FileOutputStream(outputPath);
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            File file = new File(getPathName());
            FileInputStream fileInput = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int count;
            fileOut.write(iv);
            fileOut.write(saltPass);
            fileOut.write(plainTextHash);
            fileOut.write(saltPlain);
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] passwordHash(byte[] passBytes, byte[] salt) {
        byte[] encodedHash = new byte[0];
        try {
            Hasher hasher = jargon2Hasher().type(Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536) // 64MB memory cost
                    .timeCost(3) // 3 passes through memory
                    .parallelism(4) // use 4 lanes and 4 threads
                    .hashLength(32); // 32 bytes output hash
            encodedHash = hasher.salt(salt).password(passBytes).rawHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedHash;
    }

    // TODO: Develop a regexp for the password requirements
    private static String promptForPassword() {
        String password = "";
        try {
            Scanner in = new Scanner(System.in);
            do {
                System.out.println("Enter a password");
                password = in.nextLine();
            } while (password.length() <= 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
}