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

public class decrypt {
    private byte[] iv = new byte[constants.IVLEN];
    private byte[] saltPlain = new byte[constants.SALTLEN];
    private byte[] saltPass = new byte[constants.SALTLEN];
    private byte[] plainHash = new byte[constants.HASHLEN];

    public void decryptFile(char[] password, String filePath) {
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
            // TODO: Fix naming system to rename it back to old name
            FileOutputStream fileOut = new FileOutputStream(filePath + ".test");
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            File file = new File(filePath);
            FileInputStream fileInput = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int count;
            // Gets rid of the prepend data.
            int prepData = iv.length + saltPlain.length + saltPass.length + plainHash.length;
            count = fileInput.read(new byte[prepData]);
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkKey(char[] key, String filePath) {
        boolean matches = false;
        try (ByteArray keyBytes = toByteArray(key)) {
            Arrays.fill(key, ' ');
            Verifier verifier = jargon2Verifier().type(Type.ARGON2id) // Data-dependent hashing
                    .memoryCost(constants.MEMORYCOST) // 128MB memory cost
                    .timeCost(constants.TIMECOST) // 30 passes through memory
                    .parallelism(constants.PARALLELISM); // use 4 lanes and 4 threads
            getPrependData(filePath);

            matches = verifier.hash(plainHash).salt(saltPlain).password(keyBytes).verifyRaw();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    private void getPrependData(String filePath) {
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

    private byte[] getPassHash(ByteArray passBytes, byte[] salt) {
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