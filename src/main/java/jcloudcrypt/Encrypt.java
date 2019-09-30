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

public class Encrypt {

    /** Initialization vector byte array for AES in CBC mode */
    private byte[] iv = new byte[Variables.IVLEN];
    /** Salt byte array for password hash stored in file header */
    private byte[] saltPlain = new byte[Variables.SALTLEN];
    /** Salt byte array for password hash used for encryption */
    private byte[] saltPass = new byte[Variables.SALTLEN];
    /**
     * Length of filename byte array to be stored in header, used during filename
     * obfuscation
     */
    private byte[] filenameLen;
    /** Original filename byte array */
    private byte[] filenameBytes;
    /** Password hash to be stored in file header */
    private byte[] plainTextHash;
    /** Memory Cost used to hash password */
    private byte[] memCostArray;
    /** Parallelism setting used to hash password */
    private byte[] parArray;
    /** Time Cost setting used to hash password */
    private byte[] timeCostArray;
    /** Flag for filename obfuscation */
    private byte obFlag;
    /** Obfuscated filepath including new filename. Mostly used for testing only. */
    private String obfFilePath;

    /**
     * Driver for file encryption. Encrypts file in AES-256 with Argon2 password
     * hashing.
     * 
     * @param password      char array containing user's password to encrypt the
     *                      file with
     * @param filePath      String path to the file to be encrypted
     * @param obfuscateName boolean flag whether the filename should be encrypted or
     *                      not
     * @param memoryCost    int memory cost assosciated with the hashing algorithm
     *                      in KB
     * @param parallelism   int number of lanes and threads used for hashing
     * @param timeCost      int number of passes through memory for the hashing
     *                      algorithm
     * @return completion status int
     */
    public int encryptFile(char[] password, String filePath, boolean obfuscateName, int memoryCost, int parallelism,
            int timeCost) {
        if (password == null)
            return 5;
        if (!checkFileExists(filePath)) {
            Arrays.fill(password, ' ');
            return 2;
        }
        FileInputStream fileInput = null;
        CipherOutputStream cipherOut = null;
        FileOutputStream fileOut = null;
        File file = null;
        obFlag = (obfuscateName) ? (byte) 1 : (byte) 0;
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        secureRandom.nextBytes(saltPlain);
        secureRandom.nextBytes(saltPass);
        Cipher cipher;
        byte[] passwordHash = null;
        try (ByteArray passBytes = toByteArray(password).clearSource()) {
            plainTextHash = passwordHash(passBytes, saltPlain, memoryCost, parallelism, timeCost);
            passwordHash = passwordHash(passBytes, saltPass, memoryCost, parallelism, timeCost);
            cipher = buildCipher(passwordHash, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            if (passwordHash != null)
                Arrays.fill(passwordHash, (byte) 0); // clears out the password hash
        }
        buildSettingArrays(memoryCost, parallelism, timeCost);
        file = new File(filePath);
        if (obfuscateName) {
            grabFilename(file);
        }
        String outputPath = outputPath(filePath);
        try {
            fileOut = new FileOutputStream(outputPath);
            cipherOut = new CipherOutputStream(fileOut, cipher);
            fileInput = new FileInputStream(file);
            if (obfuscateName) {
                grabFilename(file);
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            return 2;
        }
        int writeReturn = writeToFile(fileOut, fileInput, cipherOut);
        if (writeReturn != 0) // Fails to write to file
            return writeReturn;
        return 0;
    }

    /**
     * Builds the cipher for encryption.
     * 
     * @param passwordHash byte array containing the hashed password used for cipher
     * @param iv           byte array containing the initialization vector for AES
     *                     in CBC mode
     * @return built Cipher object
     */
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

    /**
     * Reads file data and writes all header data and encrypted file data to the
     * encrypted file. Closes all passed streams before returning.
     * 
     * @param fileOut   FileOutputStream for encrypted file to be written to. Writes
     *                  header information to file.
     * @param fileInput FileInputStream of file read from
     * @param cipherOut CipherOutputStream for encrypted file to be written to.
     * @return completion status int
     */
    private int writeToFile(FileOutputStream fileOut, FileInputStream fileInput, CipherOutputStream cipherOut) {
        byte[] buffer = new byte[2048];
        int count, returnVal = 0;
        try {
            fileOut.write(obFlag);
            if (obFlag == 1)
                fileOut.write(filenameLen);
            fileOut.write(iv);
            fileOut.write(saltPass);
            fileOut.write(plainTextHash);
            fileOut.write(saltPlain);
            fileOut.write(memCostArray);
            fileOut.write(parArray);
            fileOut.write(timeCostArray);
            if (obFlag == 1) { // Writes filename to buffer to be encrypted!
                for (int n = 0; n < filenameBytes.length; n++) {
                    buffer[n] = filenameBytes[n];
                }
                int miniBufferLen = 2048 - filenameBytes.length;
                byte[] miniBuffer = new byte[miniBufferLen];
                count = fileInput.read(miniBuffer);
                for (int n = 0; n < count; n++) {
                    buffer[filenameBytes.length + n] = miniBuffer[n];
                }
                cipherOut.write(buffer, 0, count + filenameBytes.length);
            }
            while ((count = fileInput.read(buffer)) > 0) {
                cipherOut.write(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnVal = 1;
        } finally {
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
        }
        return returnVal;
    }

    /**
     * Hashes user given password to be used for encryption. Hashes using Argon2
     * hashing algorithm.
     * 
     * @param passBytes   ByteArray object containing user password
     * @param salt        byte array containing the randomly generated salt for
     *                    hashing
     * @param memoryCost  int memory cost value set by user
     * @param parralelism int parallelism value set by user
     * @param timeCost    int time cost value set by user
     * @return hashed password as a byte array
     */
    private byte[] passwordHash(ByteArray passBytes, byte[] salt, int memoryCost, int parallelism, int timeCost) {
        byte[] hash = new byte[0];
        Hasher hasher = jargon2Hasher().type(Type.ARGON2id) // Data-dependent hashing
                .memoryCost(memoryCost) // 128MB memory cost
                .timeCost(timeCost) // 30 passes through memory
                .parallelism(parallelism) // use 4 lanes and 4 threads
                .hashLength(Variables.HASHLEN); // 32 bytes output hash
        hash = hasher.salt(salt).password(passBytes).rawHash();
        return hash;
    }

    /**
     * Generates a 25 character long random name for encryption using filename
     * obfuscation.
     * 
     * @return random filename String
     */
    private String generateName() {
        String name = "";
        for (int n = 0; n < 25; n++) {
            int value = (int) (Math.random() * 26) + 97;
            char letter = (char) value;
            name += letter;
        }
        return name;
    }

    /**
     * Converts filename of the file to be encrypted to a byte array and sets the
     * filename length.
     * 
     * @param file File object pointing to file
     */
    private void grabFilename(File file) {
        String name = file.getName();
        filenameBytes = name.getBytes();
        short filenameLenShort = (short) filenameBytes.length;
        filenameLen = ByteBuffer.allocate(2).putShort(filenameLenShort).array();

    }

    /**
     * Checks if the file of the given file path exists and is a file.
     * 
     * @param filePath String containing the file path
     * @return returns true if file exists and is file
     */
    private boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.isFile();
    }

    /**
     * Sets the new output path for the encrypted file.
     * 
     * @param filepath String containing path to the file for encryption
     * @return new path String for the encrypted file
     */
    private String outputPath(String filepath) {
        String outputPath = null;
        if (obFlag == 1) {
            File file = new File(filepath);
            outputPath = file.getParent();
            outputPath = outputPath + File.separator + generateName() + ".jcc";
            obfFilePath = outputPath;
        } else {
            outputPath = filepath + ".jcc";
        }
        return outputPath;
    }

    /**
     * Builds the hashing settings arrays to be stored in the file header
     * 
     * @param memCost     Memory Cost setting int
     * @param parallelism Parallelism setting int
     * @param timeCost    Time Cost setting int
     */
    private void buildSettingArrays(int memCost, int parallelism, int timeCost) {
        memCostArray = ByteBuffer.allocate(4).putInt(memCost).array();
        parArray = ByteBuffer.allocate(4).putInt(parallelism).array();
        timeCostArray = ByteBuffer.allocate(4).putInt(timeCost).array();
    }

    /**
     * Used for unit tests only.
     * 
     * @return path to file String
     */
    String getObfFilePath() {
        return obfFilePath;
    }
}