package jcloudcrypt;

import java.io.Console;
import java.io.File;
import java.util.Arrays;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

// TODO: Get test cases setup
// TODO: Format code!
// TODO: Document code with comments for possible other devs
// TODO: Get error messages setup for user.
// TODO: Get a an encrypted keys file setup
// TODO: UI needs to look nicer. Functional UI currently in place
// TODO: Implement an "Advanced" settings option - allow user to set hashing settings, etc.
// TODO: Create a regexp for the password requirements - maybe
// TODO: Strip out UI, spin-off JCloudCryptCLI and a GUI wrapper for it.

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
            status = processCall(arguments);
            System.out.println(status);
        }
    }

    private static int processCall(Arguments arguments) {
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
        arguments.fixVariables();
        Console console = System.console();
        boolean matches = false;
        char[] password = null;
        char[] passwordVerify = null;
        while (!matches) {
            password = console.readPassword("Password: "); // Gets cleared in Encryption / Decryption method
            passwordVerify = console.readPassword("Verify Password: ");
            matches = Arrays.equals(password, passwordVerify);
        }
        if (passwordVerify != null)
            Arrays.fill(passwordVerify, ' ');
        String filePath = arguments.getFilePath();
        if (checkFileExists(filePath)) {
            int returnVal = 0;
            if (arguments.getSelection() == 'e') {
                Encrypt encryption = new Encrypt();
                returnVal = encryption.encryptFile(password, filePath, false);
            } else if (arguments.getSelection() == 'd') {
                Decrypt decryption = new Decrypt();
                returnVal = decryption.decryptFile(password, filePath);
            }
            return returnVal;
        } else
            return 6;
    }

    private static boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.isFile();
    }
}