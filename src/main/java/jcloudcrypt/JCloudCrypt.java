package jcloudcrypt;

import java.io.Console;
import java.util.Arrays;

/*
File Header Layout:
# of Bytes  -   Useage
-----------------------
1               Random Name Flag
2*              Length of filename
16              Initialization Vector
32              Salt for Password
32              2nd Hashing of password
32              Salt for 2nd Hashed password
4               Memory Cost
4               Parallelism Value
4               Time Cost

*only present if Random Name Flag = 1
*/

// TODO: Get test cases setup
// TODO: Strip out UI, spin-off JCloudCrypt CLI and a GUI wrapper for it.
// TODO: Format code!
// TODO: Get error messages setup for user.
// TODO: Document code with comments for possible other devs
// TODO: Create a regexp for the password requirements - maybe
// TODO: Adjust Encrypt/Decrypt methods to allow user-set settings
// TODO: Get a an encrypted keys file setup

public class JCloudCrypt {
    private static final int defaultMemoryCost = 65536;
    private static final int defaultTimeCost = 10;
    private static final int defaultParallelism = 4;

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
            // arguments.fixVariables();
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
        int returnVal = 0, memCost = defaultMemoryCost, parallel = defaultParallelism, timeCost = defaultTimeCost;
        String filePath = arguments.getFilePath();
        if (arguments.hasOption("memCost"))
            memCost = Integer.parseInt(arguments.getOptionValue("memCost"));
        if (arguments.hasOption("parallelism"))
            parallel = Integer.parseInt(arguments.getOptionValue("parallelism"));
        if (arguments.hasOption("timeCost"))
            timeCost = Integer.parseInt(arguments.getOptionValue("timeCost"));
        if (arguments.getSelection() == 'e') {
            Encrypt encryption = new Encrypt();
            if (arguments.hasOption("obfuscate"))
                returnVal = encryption.encryptFile(password, filePath, true, memCost, parallel, timeCost);
            else
                returnVal = encryption.encryptFile(password, filePath, false, memCost, parallel, timeCost);
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