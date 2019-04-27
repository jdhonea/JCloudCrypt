package cloudcrypt;

import java.util.Scanner;
import java.io.File;

public class pathReader {
    private boolean isFile = false;
    private boolean isDirectory = false;
    private String pathName;

    public pathReader() {
        // Default Constructor
    }

    public String getPathName() {
        return pathName;
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void readPath() {
        System.out.println("Path to file or directory: ");
        try {
            Scanner pathScanner = new Scanner(System.in);
            boolean exists = false;
            String pathName;
            File file;
            do {
                pathName = pathScanner.nextLine();
                file = new File(pathName);
                exists = file.exists();
            } while (!exists);
            this.isFile = file.isFile();
            this.isDirectory = file.isDirectory();
            this.pathName = pathName;
        } catch (Exception e) {
            System.out.println("Scanner exception caught.");
        }
    }

}