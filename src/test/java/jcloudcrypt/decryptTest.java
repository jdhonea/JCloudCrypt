package jcloudcrypt;

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
            String text = new String(
                    "This is a test of the encryption method. This is not a very rigorous test but it is a test nonetheless.");
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
        encrypt encryption = new encrypt();
        encryption.encryptFile(pass, path, false);
        file.delete();
        path = path + ".jcc";
        decrypt decryption = new decrypt();
        decryption.decryptFile(pass2, path);
        try {
            assertTrue(FileUtils.contentEquals(file, file2));
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}