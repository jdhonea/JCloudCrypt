package jcloudcrypt;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

// TODO: Get test cases setup
// TODO: Format code!
// TODO: Get error messages setup for user.
// TODO: Get a an encrypted keys file setup
// TODO: UI needs to look nicer. Functional UI currently in place
// TODO: Implement an "Advanced" settings option - allow user to set hashing settings, etc.
// TODO: Create a regexp for the password requirements - maybe
// TODO: Strip out UI, spin-off JCloudCryptCLI and a GUI wrapper for it.

public class jcloudCrypt {
    public static int main(String[] args) {

        // New Window()
        // ui ui = new ui();
        // ui.mainWindow();

        Arguments arguments = new Arguments(args);
        arguments.printHelp();

        return 0;
    }
}