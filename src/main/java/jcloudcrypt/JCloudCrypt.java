package jcloudcrypt;

import java.io.Console;
import java.util.Arrays;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

// TODO: Get test cases setup
// TODO: Format code!
// TODO: Document code
// TODO: Get error messages setup for user.
// TODO: Create a method to allow users to securely delete the original file.
// TODO: Implement an "Advanced" settings option - allow user to set hashing settings, etc.
// TODO: Create a regexp for some basic password requirements
// TODO: Get an encrypted keys file setup, will allow user to safely store keys for multiple files.
// TODO: Create a GUI wrapper for the project.

public class JCloudCrypt {
    public static void main(String[] args) {
        int status = 0;
        Arguments arguments = new Arguments(args);
        if (arguments.hasArgumentErrors()) {
            System.out.println("Incorrect Useage.");
            arguments.printHelp();
            status = 1;
        } else if (args.length == 0 || arguments.hasOption("help")) {
            arguments.printHelp();
            status = 0;
        } else {
            status = arguments.runArgumentChecks(arguments);
            arguments.fixVariables();
            if (status == 0) {
                char[] password = (arguments.getSelection() == 'e') ? readEncryptPassword()
                        : readDecryptPassword(arguments.getFilePath());
                if (password != null)
                    status = objectFactory(arguments, password);
            }
        }
    }

    /**
     * Creates the encryption / decryption object and runs the encryption /
     * decryption step.
     * 
     * @param arguments Arguments object containing parsed arguments
     * @param password  char array containing user's password, gets cleared during
     *                  encryption / decryption step
     * @return error status
     */
    static int objectFactory(Arguments arguments, char[] password) {
        int returnVal = 0;
        String filePath = arguments.getFilePath();
        if (arguments.getSelection() == 'e') {
            Encrypt encryption = new Encrypt();
            if (arguments.hasOption("obfuscate"))
                returnVal = encryption.encryptFile(password, filePath, true);
            else
                returnVal = encryption.encryptFile(password, filePath, false);
        } else if (arguments.getSelection() == 'd') {
            Decrypt decryption = new Decrypt();
            returnVal = decryption.decryptFile(password, filePath);
        }
        return returnVal;
    }

    /**
     * Reads password and password verifcation from user for file encryption.
     * 
     * @return user's password in a char array, null if not verified
     */
    static char[] readEncryptPassword() {
        Console console = System.console();
        char[] password = null;
        char[] passwordVerify = null;
        int count = 3;
        while (count > 0) {
            password = console.readPassword("Password: "); // Gets cleared in Encryption / Decryption method
            passwordVerify = console.readPassword("Verify Password: ");
            boolean matches = Arrays.equals(password, passwordVerify);
            if (matches)
                return password;
            else
                System.out.println("Passwords do not match.");
            count--;
        }
        if (password != null)
            Arrays.fill(password, ' ');
        if (passwordVerify != null)
            Arrays.fill(passwordVerify, ' ');
        return null;
    }

    /**
     * Reads user's password for file decryption and verifies it with password
     * stored in file header.
     * 
     * @param filePath path to file to be decrypted
     * @return user's password in char array if password is verified, null if not
     *         verified
     */
    static char[] readDecryptPassword(String filePath) {
        Console console = System.console();
        char[] password = null;
        int count = 3;
        while (count > 0) {
            password = console.readPassword("Password: ");
            Decrypt decryption = new Decrypt();
            boolean matches = decryption.checkKey(password, filePath);
            if (matches)
                return password;
            else
                System.out.println("Password is incorrect.");
            count--;
        }
        return null;
    }
}