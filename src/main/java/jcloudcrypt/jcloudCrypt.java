package jcloudcrypt;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

// TODO: Get test cases setup
// TODO: Format code!
// TODO: Get a keys file setup
// TODO: Allow obfuscating the filename, encrypting it and storing it in the beginning of the file
// TODO: UI needs to look nicer. Functional UI currently in place
// TODO: Implement an "Advanced" settings option
// TODO: Implement a feature that allows the user to adjust # of passes through memory
// TODO: Create a regexp for the password requirements - maybe
// TODO: Restructure *all* of UI code
// TODO: Would like to implement my own Argon2 api eventually

public class jcloudCrypt {
    public static void main(String[] args) {

        // byte[] saltPlain = new byte[128];
        // ByteArray passBytes = toByteArray("Dupert");
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