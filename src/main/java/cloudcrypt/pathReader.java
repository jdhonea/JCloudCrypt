package cloudcrypt;

import java.util.Scanner;
import java.io.File;

public class pathReader {
    private boolean isFile = false;
    private boolean isDirectory = false;
    private String pathName;

    public pathReader() {
        // Default Constructor
        readPathName();
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

    private void readPathName() {
        System.out.println("Path to file or directory: ");
        Scanner in = new Scanner(System.in);
        boolean exists = false;
        String pathName;
        File file;
        do {
            pathName = in.nextLine();
            file = new File(pathName);
            exists = file.exists();
        } while (!exists);
        // in.close();
        isFile = file.isFile();
        isDirectory = file.isDirectory();
        this.pathName = pathName;
    }
}