package jcloudcrypt;

import static com.kosprov.jargon2.api.Jargon2.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//TODO: Implement decrypt
public class decrypt {
    static byte[] iv = new byte[16];
    static byte[] saltPlain = new byte[128];
    static byte[] saltPass = new byte[128];
    static byte[] plainHash = new byte[32];

    public static void decryptFile(char[] password, String filePath) {
        try {
            ByteArray passBytes = toByteArray(password);
            Arrays.fill(password, ' ');
            getPrependData(filePath);
            byte[] passHash = getPassHash(passBytes, saltPass);
            passBytes.clear();
            SecretKeySpec skey = new SecretKeySpec(passHash, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
            FileOutputStream fileOut = new FileOutputStream(filePath + ".test");
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            File file = new File(filePath);
            FileInputStream fileInput = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int count;
            // Gets rid of the prepend data.
            count = fileInput.read(new byte[304]);
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkKey(char[] key, String filePath) {
        try (ByteArray keyBytes = toByteArray(key)) {
            Arrays.fill(key, ' ');
            Verifier verifier = jargon2Verifier().type(Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536) // 64MB memory cost
                    .timeCost(3) // 3 passes through memory
                    .parallelism(4); // use 4 lanes and 4 threads
            getPrependData(filePath);

            boolean matches = verifier.hash(plainHash).salt(saltPlain).password(keyBytes).verifyRaw();
            System.out.println(matches);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void getPrependData(String filePath) {
        try {
            File inFile = new File(filePath);
            FileInputStream file = new FileInputStream(inFile);
            int count = file.read(iv);
            if (count > 0) {
                count = file.read(saltPass);
            }
            if (count > 0) {
                count = file.read(plainHash);
            }
            if (count > 0) {
                count = file.read(saltPlain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] getPassHash(ByteArray passBytes, byte[] salt) {
        byte[] hash = new byte[0];
        try {
            Hasher hasher = jargon2Hasher().type(Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536) // 64MB memory cost
                    .timeCost(3) // 3 passes through memory
                    .parallelism(4) // use 4 lanes and 4 threads
                    .hashLength(32); // 32 bytes output hash
            hash = hasher.salt(salt).password(passBytes).rawHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

}