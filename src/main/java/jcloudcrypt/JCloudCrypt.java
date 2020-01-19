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

// TODO: Get in-depth test cases setup
// TODO: Get error messages setup for user.
// TODO: Create a method to allow users to securely delete the original file.
// TODO: Create a regexp for some basic password requirements
// TODO: Get an encrypted keys file setup, will allow user to safely store keys for multiple files.
// TODO: Create a GUI wrapper for the project.

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
            if (status == 0) {
                if (arguments.getSelection() != 'c') {
                    char[] password = (arguments.getSelection() == 'e') ? readEncryptPassword()
                            : readDecryptPassword(arguments.getFilePath());
                    if (password != null)
                        status = objectFactory(arguments, password);
                } else {
                    char[] key = readCheckKey();
                    runCheckKey(arguments.getFilePath(), key);
                }
            } else {
                System.out.println("Error: Conditions out of bounds!");
            }
        }
        System.exit(status);
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
            if (arguments.hasOption("r"))
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
     * Reads password and password verifcation from user for file encryption. Gives
     * user 3 attempts before failing. Upon failure, a null value is returned.
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

    /**
     * Reads the key from console for the key check.
     * 
     * @return char array containing the key guess.
     */
    static char[] readCheckKey() {
        Console console = System.console();
        char[] key = console.readPassword("Password: ");
        return key;
    }

    /**
     * Wrapper for the check key function. Used when the user calls the check key
     * option from command line. Exits the JVM and returns the status.
     * 
     * @param filePath String containing path to file
     * @param key      Key to be tested in a char array
     */
    static void runCheckKey(String filePath, char[] key) {
        Decrypt decrypt = new Decrypt();
        int status = (decrypt.checkKey(key, filePath)) ? 0 : 1;
        if (status == 0) {
            System.out.println("Key is valid!");
        } else {
            System.out.println("Invalid key.");
        }
        System.exit(status);
    }
}