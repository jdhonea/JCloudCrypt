package jcloudcrypt;

import java.security.SecureRandom;
import static com.kosprov.jargon2.api.Jargon2.*;

//TODO: Get test cases setup
//TODO: Get a keys file setup
//TODO: Using keys file allows obstructing file name

public class jcloudCrypt {
    public static void main(String[] args) {
        byte[] saltPlain = new byte[128];
        ByteArray passBytes = toByteArray("Dupert");
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(saltPlain);
        long startTime = System.nanoTime();
        encrypt.passwordHash(passBytes, saltPlain);
        long endTime = System.nanoTime();
        System.out.println((endTime - startTime) / 1000000);
        // New Window()
        // ui ui = new ui();
        // ui.mainWindow();
    }
}