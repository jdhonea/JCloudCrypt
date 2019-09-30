package jcloudcrypt;

// TODO: Will no longer be needed after Encryption / Decryption redesign. Hash settings will be stored in file header during encryption.
public class Variables {
    public static int SALTLEN = 32; // Length of salts in bytes
    public static int HASHLEN = 32; // Length of hash in bytes
    public static int IVLEN = 16; // Length of Init. Vector in bytes
}