package jcloudcrypt;

import java.io.Console;
import java.io.File;
import java.util.Arrays;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

// TODO: Get test cases setup
// TODO: Strip out UI, spin-off JCloudCrypt CLI and a GUI wrapper for it.
// TODO: Format code!
// TODO: Get error messages setup for user.
// TODO: Implement an "Advanced" settings option - allow user to set hashing settings, etc.
// TODO: Document code with comments for possible other devs
// TODO: Create a regexp for the password requirements - maybe
// TODO: Adjust Encrypt/Decrypt methods to allow user-set settings
// TODO: Get a an encrypted keys file setup

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
            status = runArgumentChecks(arguments);
            arguments.fixVariables();
            if (status == 0) {
                char[] password = (arguments.getSelection() == 'e') ? readEncryptPassword()
                        : readDecryptPassword(arguments.getFilePath());
                if (password != null)
                    status = processCall(arguments, password);
            }
        }
    }

    static int runArgumentChecks(Arguments arguments) {
        if (arguments.checkForConflicts()) {
            return 2;
        }
        if (arguments.checkOutOfMemBounds()) {
            return 3;
        }
        if (arguments.checkOutOfParallelismBounds()) {
            return 4;
        }
        if (arguments.checkOutOfTimeCostBounds()) {
            return 5;
        }
        String filePath = arguments.getFilePath();
        if (checkFileDoesNotExist(filePath)) {
            return 6;
        }
        return 0;
    }

    static int processCall(Arguments arguments, char[] password) {
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

    private static boolean checkFileDoesNotExist(String filePath) {
        File file = new File(filePath);
        return !file.isFile();
    }

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