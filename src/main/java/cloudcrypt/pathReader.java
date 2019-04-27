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

    public void readPathName() {

    }
}