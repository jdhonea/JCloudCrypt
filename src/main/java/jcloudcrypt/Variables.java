package jcloudcrypt;

public class Variables {
    public static int SALTLEN = 32; // Length of salts in bytes
    public static int HASHLEN = 32; // Length of hash in bytes
    public static int IVLEN = 16; // Length of Init. Vector in bytes
    public static int MEMORYCOST = 65536; // Memory cost in KB
    public static int PARALLELISM = 4; // # of lanes and threads
    public static int TIMECOST = 10; // # of passes through memory
}