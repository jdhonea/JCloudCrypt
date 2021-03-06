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

public class Decrypt {
    /** AES initialization vector byte array */
    private byte[] iv = new byte[Variables.IVLEN];
    /** Unencrypted password hash's salt byte array */
    private byte[] saltPlain = new byte[Variables.SALTLEN];
    /** Salt byte array for hashed password for AES encryption */
    private byte[] saltPass = new byte[Variables.SALTLEN];
    /** Unencrypted hashed password byte array for password verification */
    private byte[] plainHash = new byte[Variables.HASHLEN];
    /** Memory Cost setting */
    private int memoryCost;
    /** Parallelism setting */
    private int parallelism;
    /** Time Cost setting */
    private int timeCost;
    /** Flag for filename obfuscation option */
    private byte obFlag;
    /** Length of original filename. */
    private short filenameLen;

    /**
     * Driver for file decryption. Encrypted using AES-256 in CBC mode.
     * 
     * @param password char array containing the user-provided password
     * @param filePath String containing the path to the file being decrypted
     * @return completion status int
     */
    public int decryptFile(char[] password, String filePath) {

        if (password == null)
            return 5;
        int prependReturn = getHeaderData(filePath);
        if (prependReturn != 0)
            return 1;
        Cipher cipher;
        byte[] passHash = null;
        try (ByteArray passBytes = toByteArray(password).clearSource()) {
            passHash = getPassHash(passBytes, saltPass);
            cipher = buildCipher(passHash);
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        } finally {
            if (passHash != null)
                Arrays.fill(passHash, (byte) 0); // clears out the password hash
        }
        if (obFlag == 1) {
            int writeReturn = obfWriteToFile(filePath, cipher);
            if (writeReturn != 0)
                return 3;
        } else {
            int writeReturn = normWriteToFile(filePath, cipher);
            if (writeReturn != 0)
                return writeReturn;
        }
        Arrays.fill(password, ' ');
        return 0;
    }

    /**
     * Verifies user-provided password with hashed password stored in header.
     * 
     * @param key      char array containing password being verified
     * @param filePath String containing path to the file being decrypted
     * @return true if key is verified
     */
    public boolean checkKey(char[] key, String filePath) {
        boolean matches = false;
        try (ByteArray keyBytes = toByteArray(key)) {
            getHeaderData(filePath);
            Verifier verifier = jargon2Verifier().type(Type.ARGON2id) // Data-dependent hashing
                    .memoryCost(memoryCost) // 128MB memory cost
                    .timeCost(timeCost) // 30 passes through memory
                    .parallelism(parallelism); // use 4 lanes and 4 threads
            matches = verifier.hash(plainHash).salt(saltPlain).password(keyBytes).verifyRaw();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    /**
     * Builds decryption Cipher using user's password and initialization vector
     * stored in file header.
     * 
     * @param passHash ByteArray object containing user's hashed password
     * @return decryption Cipher
     */
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

    /**
     * Grabs the header information from the file being decrypted.
     * 
     * @param filePath String containing path to the file being decrypted
     * @return completion status int
     */
    private int getHeaderData(String filePath) {
        FileInputStream file = null;
        try {
            File inFile = new File(filePath);
            file = new FileInputStream(inFile);
            obFlag = (byte) file.read();
            if (obFlag == 1) {
                byte[] shortBytes = new byte[2];
                file.read(shortBytes);
                filenameLen = ByteBuffer.wrap(shortBytes).getShort();
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
            if (count > 0) {
                byte[] tempArray = new byte[Integer.SIZE / 8];
                count = file.read(tempArray);
                memoryCost = ByteBuffer.wrap(tempArray).getInt();
            }
            if (count > 0) {
                byte[] tempArray = new byte[Integer.SIZE / 8];
                count = file.read(tempArray);
                parallelism = ByteBuffer.wrap(tempArray).getInt();
            }
            if (count > 0) {
                byte[] tempArray = new byte[Integer.SIZE / 8];
                count = file.read(tempArray);
                timeCost = ByteBuffer.wrap(tempArray).getInt();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 2;
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
                e.printStackTrace();
                return 3;
            }
        }
        return 0;
    }

    /**
     * Generates the password hash from user provided password and the salt stored
     * in the file header. Hashes using Argon2 hashing algorithm.
     * 
     * @param passBytes user password stored in a ByteArray
     * @param salt      byte array containing original salt used during file
     *                  encryption
     * @return generated password hash byte array
     */
    private byte[] getPassHash(ByteArray passBytes, byte[] salt) {
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
     * Writes decrypted data to output file. Used when filename encryption is not
     * used.
     * 
     * @param filePath String containing path to the file to be written to
     * @param cipher   Decryption Cipher used for the decryption process
     * @return completion status
     */
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
        int prepData = iv.length + saltPlain.length + saltPass.length + plainHash.length + 13;
        try {
            count = fileInput.read(new byte[prepData]);
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
            } catch (IOException e) {
                e.printStackTrace();
                returnVal = 2;
            }
        }
        return returnVal;
    }

    /**
     * Writes decrypted data to output file. Used when filename encryption is used.
     * 
     * @param filePath String path to the file to be written to
     * @param cipher   Decryption Cipher used for file decryption
     * @return completion status int
     */
    private int obfWriteToFile(String filePath, Cipher cipher) {
        int returnVal = 0, count = 0;
        int prepData = iv.length + saltPlain.length + saltPass.length + plainHash.length + 15;
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
                byte[] filenameBytes = new byte[filenameLen];
                byte[] miniBuffer = new byte[count - filenameLen];
                for (int n = 0; n < filenameLen; n++) {
                    filenameBytes[n] = buffer[n];
                }
                for (int n = 0; n < miniBuffer.length; n++) {
                    miniBuffer[n] = buffer[n + filenameLen];
                }
                String newName = new String(filenameBytes);
                fileOutput = new FileOutputStream(filePathParent + File.separator + newName);
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
        } finally {
            try {
                if (cipherIn != null)
                    cipherIn.close();
                if (fileOutput != null)
                    fileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
                returnVal = 4;
            }
        }
        return returnVal;
    }
}