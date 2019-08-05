package jcloudcrypt;

public class Constants {
    public static final int SALTLEN = 32; // Length of salts in bytes
    public static final int HASHLEN = 32; // Length of hash in bytes
    public static final int IVLEN = 16; // Length of Init. Vector in bytes
    public static final int MEMORYCOST = 65536; // Memory cost in KB
    public static final int PARALLELISM = 4; // # of lanes and threads
    public static final int TIMECOST = 10; // # of passes through memory
}