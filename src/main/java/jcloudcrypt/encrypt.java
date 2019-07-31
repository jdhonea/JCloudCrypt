package jcloudcrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import static com.kosprov.jargon2.api.Jargon2.*;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class encrypt {

    private byte[] iv = new byte[constants.IVLEN];
    private byte[] saltPlain = new byte[constants.SALTLEN];
    private byte[] saltPass = new byte[constants.SALTLEN];
    private byte[] fileNameLen;
    private byte[] fileNameBytes;
    private byte[] plainTextHash;
    private byte[] passwordHash;
    private byte obFlag;
    private String obfFilePath;

    public int encryptFile(char[] password, String filePath, boolean obfuscateName) {
        FileInputStream fileInput = null;
        CipherOutputStream cipherOut = null;
        FileOutputStream fileOut = null;
        File file = null;
        obFlag = (obfuscateName) ? (byte) 1 : (byte) 0;
        if (password == null)
            return 5;
        obFlag = (obfuscateName) ? (byte) 1 : (byte) 0;
        ByteArray passBytes = toByteArray(password);
        Arrays.fill(password, ' '); // clears out the plain text password
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        secureRandom.nextBytes(saltPlain);
        secureRandom.nextBytes(saltPass);
        plainTextHash = passwordHash(passBytes, saltPlain);
        passwordHash = passwordHash(passBytes, saltPass);
        try {
            passBytes.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        Cipher cipher = buildCipher(passwordHash, iv);
        file = new File(filePath);
        if (obfuscateName) {
            grabFileName(file);
        }
        String outputPath = outputPath(filePath);
        try {
            fileOut = new FileOutputStream(outputPath);
            cipherOut = new CipherOutputStream(fileOut, cipher);
            fileInput = new FileInputStream(file);
            if (obfuscateName) {
                grabFileName(file);
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            return 2;
        }
        int writeReturn = writeToFile(fileOut, fileInput, cipherOut);
        if (writeReturn != 0) // Fails to write to file
            return 3;
        return 0;
    }

    private Cipher buildCipher(byte[] passwordHash, byte[] iv) {
        try {
            SecretKeySpec skey = new SecretKeySpec(passwordHash, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
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

    private int writeToFile(FileOutputStream fileOut, FileInputStream fileInput, CipherOutputStream cipherOut) {
        byte[] buffer = new byte[2048];
        int count, returnVal = 0;
        try {
            fileOut.write(obFlag);
            if (obFlag == 1)
                fileOut.write(fileNameLen);
            fileOut.write(iv);
            fileOut.write(saltPass);
            fileOut.write(plainTextHash);
            fileOut.write(saltPlain);
            if (obFlag == 1) { // Writes filename to buffer to be encrypted!
                for (int n = 0; n < fileNameBytes.length; n++) {
                    buffer[n] = fileNameBytes[n];
                }
                int miniBufferLen = 2048 - fileNameBytes.length;
                byte[] miniBuffer = new byte[miniBufferLen];
                count = fileInput.read(miniBuffer);
                for (int n = 0; n < count; n++) {
                    buffer[fileNameBytes.length + n] = miniBuffer[n];
                }
                cipherOut.write(buffer, 0, count + fileNameBytes.length);
            }
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
            if (fileOut != null)
                fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = 2;
        }
        return returnVal;
    }

    public byte[] passwordHash(ByteArray passBytes, byte[] salt) {
        byte[] hash = new byte[0];
        Hasher hasher = jargon2Hasher().type(Type.ARGON2id) // Data-dependent hashing
                .memoryCost(constants.MEMORYCOST) // 128MB memory cost
                .timeCost(constants.TIMECOST) // 30 passes through memory
                .parallelism(constants.PARALLELISM) // use 4 lanes and 4 threads
                .hashLength(constants.HASHLEN); // 32 bytes output hash
        hash = hasher.salt(salt).password(passBytes).rawHash();
        return hash;
    }

    private String generateName() {
        String name = "";
        for (int n = 0; n < 25; n++) {
            int value = (int) (Math.random() * 26) + 97;
            char letter = (char) value;
            name += letter;
        }
        return name;
    }

    private void grabFileName(File file) {
        String name = file.getName();
        fileNameBytes = name.getBytes();
        short fileNameLenShort = (short) fileNameBytes.length;
        fileNameLen = ByteBuffer.allocate(2).putShort(fileNameLenShort).array();

    }

    private String outputPath(String filepath) {
        String outputPath = null;
        if (obFlag == 1) {
            File file = new File(filepath);
            outputPath = file.getParent();
            // TODO: Find a better way to create this
            outputPath = outputPath + "/" + generateName() + ".jcc";
            obfFilePath = outputPath;
        } else {
            outputPath = filepath + ".jcc";
        }
        return outputPath;
    }

    protected String getObfFilePath() {
        return obfFilePath;
    }
}