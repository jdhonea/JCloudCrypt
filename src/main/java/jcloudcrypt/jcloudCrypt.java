package jcloudcrypt;

//Filename limit is 260 chars in windows, 255 in most linux filesystems
//Java 2 bytes per char

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
        // New Window()
        ui ui = new ui();
        ui.mainWindow();
    }
}