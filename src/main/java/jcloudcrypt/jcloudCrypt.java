package jcloudcrypt;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

// File Header:
// 1. Obfuscate Filename flag - 1 byte
// 2. If flag = 1 -> Length of file name - 2 bytes
// 3. Initialization Vector - 16 bytes
// 4. Password Salt - 32 bytes
// 5. Password Hashed for verification - 32 bytes
// 6. Hashed Password Salt - 32 bytes

//If file name is obfuscated, length of filename is stored in header and then
//first X bytes are stored in beginning of file data and encrypted.

// TODO: Add drive space check
// TODO: Add output path
// TODO: Get test cases setup
// TODO: Format code!
// TODO: Get error messages setup for user.
// TODO: Get a keys file setup
// TODO: Allow obfuscating the filename, encrypting it and storing it in the beginning of the file
// TODO: UI needs to look nicer. Functional UI currently in place
// TODO: Implement an "Advanced" settings option - allow user to set hashing settings, etc.
// TODO: Create a regexp for the password requirements - maybe
// TODO: Restructure *all* of UI code

public class jcloudCrypt {
    public static void main(String[] args) {

        // Hashing Time Test
        // byte[] saltPlain = new byte[128];
        // ByteArray passBytes = toByteArray("Jason");
        // SecureRandom secureRandom = new SecureRandom();
        // secureRandom.nextBytes(saltPlain);
        // long startTime = System.nanoTime();
        // encrypt.passwordHash(passBytes, saltPlain);
        // long endTime = System.nanoTime();
        // System.out.println((endTime - startTime) / 1000000);

        // New Window()
        ui ui = new ui();
        ui.mainWindow();
    }
}