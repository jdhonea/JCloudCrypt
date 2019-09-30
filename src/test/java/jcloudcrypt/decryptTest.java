package jcloudcrypt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class decryptTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void decryptionTest() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        File file2 = new File(folder, "Test2.txt");
        // String directory = file.getParent();
        FileOutputStream fileout = null;
        FileOutputStream fileout2 = null;
        try {
            fileout = new FileOutputStream(file);
            fileout2 = new FileOutputStream(file2);
            String text = new String("This is a test of the encryption method.");
            fileout.write(text.getBytes());
            fileout2.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (fileout != null)
                fileout.close();
            if (fileout2 != null)
                fileout2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        char[] pass = new char[] { 'T', 'e', 's', 't' }; // Get cleared by encryption
        char[] pass2 = new char[] { 'T', 'e', 's', 't' };
        String path = file.getAbsolutePath();
        Encrypt encryption = new Encrypt();
        encryption.encryptFile(pass, path, false, 65536, 4, 10);
        file.delete();
        path = path + ".jcc";
        Decrypt decryption = new Decrypt();
        decryption.decryptFile(pass2, path);
        try {
            assertTrue(FileUtils.contentEquals(file, file2));
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void decryptionTestMemCost() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        File file2 = new File(folder, "Test2.txt");
        // String directory = file.getParent();
        FileOutputStream fileout = null;
        FileOutputStream fileout2 = null;
        try {
            fileout = new FileOutputStream(file);
            fileout2 = new FileOutputStream(file2);
            String text = new String("This is a test of the encryption method.");
            fileout.write(text.getBytes());
            fileout2.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (fileout != null)
                fileout.close();
            if (fileout2 != null)
                fileout2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        char[] pass = new char[] { 'T', 'e', 's', 't' }; // Get cleared by encryption
        char[] pass2 = new char[] { 'T', 'e', 's', 't' };
        String path = file.getAbsolutePath();
        Encrypt encryption = new Encrypt();
        encryption.encryptFile(pass, path, false, 84000, 4, 10);
        file.delete();
        path = path + ".jcc";
        Decrypt decryption = new Decrypt();
        decryption.decryptFile(pass2, path);
        try {
            assertTrue(FileUtils.contentEquals(file, file2));
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void passwordCheckMatch() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        String path = file.getAbsolutePath();
        Encrypt encryption = new Encrypt();
        try (FileOutputStream fileout = new FileOutputStream(file)) {
            String text = new String("This is a test of the encryption method.");
            fileout.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // pass gets cleared in encryption step
        char[] pass = new char[] { 't', 'e', 's', 't' };
        encryption.encryptFile(pass, path, false, 65536, 4, 10);
        File file2 = new File(path + ".jcc");
        pass = new char[] { 't', 'e', 's', 't' };
        Decrypt decryption = new Decrypt();
        boolean result = decryption.checkKey(pass, file2.getPath());
        assertTrue(result);
    }

    @Test
    public void passwordCheckMismatch() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        String path = file.getAbsolutePath();
        Encrypt encryption = new Encrypt();
        try (FileOutputStream fileout = new FileOutputStream(file)) {
            String text = new String("This is a test of the encryption method.");
            fileout.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // pass gets cleared in encryption step
        char[] pass = new char[] { 't', 'e', 's', 't' };
        encryption.encryptFile(pass, path, false, 65536, 4, 10);
        File file2 = new File(path + ".jcc");
        pass = new char[] { 'D', 'i', 'f', 'f' };
        Decrypt decryption = new Decrypt();
        boolean result = decryption.checkKey(pass, file2.getPath());
        assertFalse(result);
    }

    @Test
    public void obfDecryptionTest() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        File file2 = new File(folder, "Test2.txt");
        // String directory = file.getParent();
        FileOutputStream fileout = null;
        FileOutputStream fileout2 = null;
        try {
            fileout = new FileOutputStream(file);
            fileout2 = new FileOutputStream(file2);
            String text = new String("This is a test of the encryption method.");
            fileout.write(text.getBytes());
            fileout2.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileout != null)
                    fileout.close();
                if (fileout2 != null)
                    fileout2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        char[] pass = new char[] { 'T', 'e', 's', 't' }; // Get cleared by encryption
        char[] pass2 = new char[] { 'T', 'e', 's', 't' };
        String path = file.getAbsolutePath();
        Encrypt encryption = new Encrypt();
        encryption.encryptFile(pass, path, true, 65536, 4, 10);
        file.delete();
        path = encryption.getObfFilePath();
        Decrypt decryption = new Decrypt();
        decryption.decryptFile(pass2, path);
        file = new File(folder, "Test.txt");
        try {
            assertTrue(FileUtils.contentEquals(file, file2));
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void noPass() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        Decrypt decryption = new Decrypt();
        int returnVal = decryption.decryptFile(null, file.getAbsolutePath());
        assertTrue(returnVal == 5);
    }
}