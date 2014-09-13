package net.balhar.ds.globalState.log;

import java.io.IOException;

/**
 *
 */
public class LogTest {
    public static void main(String[] args){
        String fileOutputPath = "testOutput";
        String[] filePaths = new String[]{"logBranch1","logBranch2","logBranch3"};
        String[] processes = new String[]{"127.0.0.1:5001","127.0.0.1:5002","127.0.0.1:5003"};
        try {
            new Log(filePaths, fileOutputPath, processes).write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
