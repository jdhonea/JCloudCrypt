package cloudcrypt;

/*
Cloud Crypt
Author: Jason Honea
Email: jhonea129@gmail.com
Website: jhonea.dev
*/

public class cloudCrypt {
    public static void main(String[] args) {
        // New Window()
        encrypt encryptor = new encrypt();
        encryptor.encryptFile();
        String encodedHash = encryptor.getEncodedHash();
        System.out.printf("Hash: %s%n", encodedHash);
        decrypt decryptor = new decrypt();
        decryptor.verifyPass(encodedHash);
    }
}