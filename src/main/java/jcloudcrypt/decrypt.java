package jcloudcrypt;

import static com.kosprov.jargon2.api.Jargon2.*;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class decrypt {
    private byte[] iv = new byte[constants.IVLEN];
    private byte[] saltPlain = new byte[constants.SALTLEN];
    private byte[] saltPass = new byte[constants.SALTLEN];
    private byte[] plainHash = new byte[constants.HASHLEN];

    public int decryptFile(char[] password, String filePath) {
        FileOutputStream fileOut = null;
        FileInputStream fileInput = null;
        ByteArray passBytes = toByteArray(password);
        Arrays.fill(password, ' ');
        int prependReturn = getPrependData(filePath);
        if (prependReturn != 0)
            return 1;
        byte[] passHash = getPassHash(passBytes, saltPass);
        passBytes.clear();
        Cipher cipher = buildCipher(passHash);
        String newFilePath = FilenameUtils.removeExtension(filePath);
        try {
            fileOut = new FileOutputStream(newFilePath);
            File file = new File(filePath);
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 2;
        }
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
        int writeReturn = writeToFile(fileInput, cipherOut);
        if (writeReturn != 0)
            return 3;
        try {
            if (fileInput != null)
                fileInput.close();
            if (cipherOut != null)
                cipherOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 4;
        }
        return 0;
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

    private Cipher buildCipher(byte[] passHash) {
        SecretKeySpec skey = new SecretKeySpec(passHash, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getPrependData(String filePath) {
        FileInputStream file = null;
        try {
            File inFile = new File(filePath);
            file = new FileInputStream(inFile);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        }
        return 0;
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

    private int writeToFile(FileInputStream fileInput, CipherOutputStream cipherOut) {
        byte[] buffer = new byte[2048];
        int count;
        // Gets rid of the prepend data.
        int prepData = iv.length + saltPlain.length + saltPass.length + plainHash.length;
        try {
            count = fileInput.read(new byte[prepData]);
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}