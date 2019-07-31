package jcloudcrypt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class encryptTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void fileNotFound() {
        char[] pass = new char[] { 'a' };
        encrypt encryption = new encrypt();
        int returnVal = encryption.encryptFile(pass, "", false);
        assertTrue(2 == returnVal);
    }

    @Test
    public void emptyPassword() {
        encrypt encryption = new encrypt();
        int returnVal = encryption.encryptFile(null, "unitTestFile", false);
        assertTrue(5 == returnVal);
    }

    // Test encryption by creating file in temporary folder
    // by using junit's TemporaryFolder
    @Test
    public void testEncForDiff() {
        File folder = null;
        try {
            folder = tempFolder.newFolder("folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(folder, "Test.txt");
        String path = file.getAbsolutePath();
        encrypt encryption = new encrypt();
        FileOutputStream fileout = null;
        try {
            fileout = new FileOutputStream(file);
            String text = new String("This is a test of the encryption method.");
            fileout.write(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (fileout != null)
                fileout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        char[] pass = new char[] { 't', 'e', 's', 't' };
        encryption.encryptFile(pass, path, false);
        File file2 = new File(path + ".jcc");
        try {
            boolean compare = FileUtils.contentEquals(file, file2);
            assertFalse(compare);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
