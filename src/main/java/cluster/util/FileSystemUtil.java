package cluster.util;

import java.io.File;
import java.io.FileNotFoundException;

public class FileSystemUtil {
    /* This function will return -1 if the file doesn't exist*/
    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.isFile()) {
            return file.length();
        } else {
            return -1;
        }
    }
}
