package cloudcrypt;

import java.util.Scanner;
import java.io.File;

public class pathReader {
    private boolean isFile = false;
    private boolean isDirectory = false;
    private String pathName;
    private String parent;

    protected pathReader() {
        // Default Constructor
    }

    protected String getPathName() {
        return pathName;
    }

    protected boolean isFile() {
        return isFile;
    }

    protected boolean isDirectory() {
        return isDirectory;
    }

    protected String getParent() {
        return parent;
    }

    protected String readPath() {
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
            } while (!exists || !file.isFile());
            this.isFile = file.isFile();
            this.isDirectory = file.isDirectory();
            this.pathName = pathName;
            this.parent = file.getParent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pathName;
    }

}