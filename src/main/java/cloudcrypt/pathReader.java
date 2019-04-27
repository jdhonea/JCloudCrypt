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
        isFile = file.isFile();
        isDirectory = file.isDirectory();
        this.pathName = pathName;
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
}