package jcloudcrypt;

public class constants {
    public static final int SALTLEN = 32; // Length of salts in bytes
    public static final int HASHLEN = 32; // Length of hash in bytes
    public static final int IVLEN = 16; // Length of Init. Vector in bytes
    public static final int MEMORYCOST = 131072; // Memory cost in KB
    public static final int PARALLELISM = 4; // # of lanes and threads
    public static final int TIMECOST = 20; // # of passes through memory
    public static final int MAXFILENAME = 60; // Max bytes for storing up to 30 char filename
}