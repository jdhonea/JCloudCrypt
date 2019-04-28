package cloudcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import static com.kosprov.jargon2.api.Jargon2.*;

import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class encrypt extends pathReader {
    String encodedHash = "";

    public void encryptFile() {
        String pathName = readPath();
        try {
            String password = promptForPassword();
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            byte[] key = new byte[32];
            secureRandom.nextBytes(iv);
            SecretKeySpec skey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Hashes password to 256 byte key
            key = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            this.encodedHash = passwordHash(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
            FileOutputStream fileOut = new FileOutputStream("outfile");
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            File file = new File(getPathName());
            FileInputStream fileInput = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int count;
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public encrypt() {
    }

    public String getEncodedHash() {
        return encodedHash;
    }

    private String passwordHash(byte[] passBytes) {
        String encodedHash = "";
        try {
            Hasher hasher = jargon2Hasher().type(Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536) // 64MB memory cost
                    .timeCost(3) // 3 passes through memory
                    .parallelism(4) // use 4 lanes and 4 threads
                    .saltLength(32) // 16 random bytes salt
                    .hashLength(32); // 16 bytes output hash
            encodedHash = hasher.password(passBytes).encodedHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedHash;
    }

    // TODO: Develop a regexp for the password requirements
    private String promptForPassword() {
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