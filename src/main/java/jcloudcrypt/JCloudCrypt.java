package jcloudcrypt;

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
    public static int main(String[] args) {
        int returnVal = 0;
        Arguments arguments = new Arguments(args);
        if (arguments.hasArgumentErrors()) {
            System.out.println("Incorrect Useage.");
            arguments.printHelp();
            return 1;
        } else if (args.length == 0 || arguments.hasOption("help")) {
            arguments.printHelp();
            return 0;
        } else {
            returnVal = processCall(arguments);
            System.out.println(returnVal);
            return returnVal;
        }
    }

    public static int processCall(Arguments arguments) {
        int returnVal = 0;
        if (checkForConflicts(arguments)) {
            return 2;
        }
        return returnVal;
    }

    private static boolean checkForConflicts(Arguments arguments) {
        if (arguments.hasOption("encrypt") && arguments.hasOption("decrypt"))
            return true;
        return false;
    }
}