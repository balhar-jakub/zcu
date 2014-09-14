package cz.zcu.fav.os.bash;

import java.io.File;

/**
 * Simple class providing functionality to work with absolute paths as well as relatives.
 *
 * @author Jakub Balhar
 * @version 0.1
 * @since 2012
 */
public class FileUtil {
    /**
     * It returns full path from either fullPath or relative path.
     *
     * @param actualPath path to absolutize
     * @return absolute path
     */
    public static String getFullPath(String actualPath){
        if(!actualPath.startsWith("/") && !actualPath.startsWith(":\\",1)){
            actualPath = System.getProperty("user.dir") + File.separator + actualPath;
        }
        return actualPath;
    }
}
