package jcloudcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import static com.kosprov.jargon2.api.Jargon2.*;

import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class encrypt {

    public static void encryptFile(char[] password, String filePath) {
        try {
            byte[] iv = new byte[constants.IVLEN];
            byte[] saltPlain = new byte[constants.SALTLEN];
            byte[] saltPass = new byte[constants.SALTLEN];
            ByteArray passBytes = toByteArray(password);
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            secureRandom.nextBytes(saltPlain);
            secureRandom.nextBytes(saltPass);
            byte[] plainTextHash = passwordHash(passBytes, saltPlain);
            byte[] passwordHash = passwordHash(passBytes, saltPass);
            passBytes.close();
            Arrays.fill(password, ' ');
            SecretKeySpec skey = new SecretKeySpec(passwordHash, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
            String outputPath = filePath + ".jcc";
            FileOutputStream fileOut = new FileOutputStream(outputPath);
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            // Write to encrypted file
            File file = new File(filePath);
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
            // END Write
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] passwordHash(ByteArray passBytes, byte[] salt) {
        byte[] hash = new byte[0];
        try {
            Hasher hasher = jargon2Hasher().type(Type.ARGON2id) // Data-dependent hashing
                    .memoryCost(constants.MEMORYCOST) // 128MB memory cost
                    .timeCost(constants.TIMECOST) // 30 passes through memory
                    .parallelism(constants.PARALLELISM) // use 4 lanes and 4 threads
                    .hashLength(constants.HASHLEN); // 32 bytes output hash
            hash = hasher.salt(salt).password(passBytes).rawHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
}