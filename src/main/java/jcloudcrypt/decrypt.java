package jcloudcrypt;

import static com.kosprov.jargon2.api.Jargon2.*;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class decrypt {
    private byte[] iv = new byte[constants.IVLEN];
    private byte[] saltPlain = new byte[constants.SALTLEN];
    private byte[] saltPass = new byte[constants.SALTLEN];
    private byte[] plainHash = new byte[constants.HASHLEN];
    private byte obFlag;
    private short fileNameLen;

    public int decryptFile(char[] password, String filePath) {

        if (password == null)
            return 5;
        ByteArray passBytes = toByteArray(password);
        Arrays.fill(password, ' ');
        int prependReturn = getPrependData(filePath);
        if (prependReturn != 0)
            return 1;
        byte[] passHash = getPassHash(passBytes, saltPass);
        passBytes.clear();
        Cipher cipher = buildCipher(passHash);
        if (obFlag == 1) {
            int writeReturn = obfWriteToFile(filePath, cipher);
            if (writeReturn != 0)
                return 3;
        } else {
            int writeReturn = normWriteToFile(filePath, cipher);
            if (writeReturn != 0)
                return 3;
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
            obFlag = (byte) file.read();
            if (obFlag == 1) {
                byte[] shortBytes = new byte[2];
                file.read(shortBytes);
                fileNameLen = ByteBuffer.wrap(shortBytes).getShort();
            }
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
        try {
            if (file != null)
                file.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 3;
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

    private int normWriteToFile(String filePath, Cipher cipher) {
        FileInputStream fileInput = null;
        FileOutputStream fileOut = null;
        int count, returnVal = 0;
        File file = new File(filePath);
        try {
            fileInput = new FileInputStream(file);
            String newFilePath = FilenameUtils.removeExtension(filePath);
            fileOut = new FileOutputStream(newFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            returnVal = 3;
        }
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
        byte[] buffer = new byte[2048];
        // Gets rid of the prepend data.
        int prepData = iv.length + saltPlain.length + saltPass.length + plainHash.length + 1;
        try {
            count = fileInput.read(new byte[prepData]);
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = 1;
        }
        try {
            if (fileInput != null)
                fileInput.close();
            if (cipherOut != null)
                cipherOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = 2;
        }
        return returnVal;
    }

    private int obfWriteToFile(String filePath, Cipher cipher) {
        int returnVal = 0, count = 0;
        int prepData = iv.length + saltPlain.length + saltPass.length + plainHash.length + 3;
        byte[] buffer = new byte[2048];
        FileInputStream fileInput = null;
        FileOutputStream fileOutput = null;
        CipherInputStream cipherIn = null;
        File file = new File(filePath);
        String filePathParent = file.getParent();
        try {
            fileInput = new FileInputStream(file);
            cipherIn = new CipherInputStream(fileInput, cipher);
            count = fileInput.read(new byte[prepData]);
            if (count > 0) {
                count = cipherIn.read(buffer);
                byte[] fileNameBytes = new byte[fileNameLen];
                byte[] miniBuffer = new byte[count - fileNameLen];
                for (int n = 0; n < fileNameLen; n++) {
                    fileNameBytes[n] = buffer[n];
                }
                for (int n = 0; n < miniBuffer.length; n++) {
                    miniBuffer[n] = buffer[n + fileNameLen];
                }
                String newName = new String(fileNameBytes);
                fileOutput = new FileOutputStream(filePathParent + "/" + newName);
                fileOutput.write(miniBuffer, 0, miniBuffer.length);
                while ((count = cipherIn.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, count);
                }
            } else {
                returnVal = 3;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            returnVal = 1;
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = 2;
        }
        try {
            if (cipherIn != null)
                cipherIn.close();
            if (fileOutput != null)
                fileOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = 4;
        }
        return returnVal;
    }
}