package cloudcrypt;

import java.util.Scanner;
import java.io.File;

public class pathReader {
    private static boolean isFile = false;
    private static boolean isDirectory = false;
    private static String pathName;
    private static String parent;

    protected static String getPathName() {
        return pathName;
    }

    protected static boolean isFile() {
        return isFile;
    }

    protected static boolean isDirectory() {
        return isDirectory;
    }

    protected static String getParent() {
        return parent;
    }

    protected static String readPath() {
        System.out.println("Path to file or directory: ");
        try {
            Scanner pathScanner = new Scanner(System.in);
            boolean exists = false;
            String path;
            File file;
            do {
                path = pathScanner.nextLine();
                file = new File(path);
                exists = file.exists();
            } while (!exists || !file.isFile());
            isFile = file.isFile();
            isDirectory = file.isDirectory();
            pathName = path;
            parent = file.getParent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pathName;
    }

}