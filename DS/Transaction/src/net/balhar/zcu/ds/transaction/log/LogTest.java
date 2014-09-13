package net.balhar.zcu.ds.transaction.log;

import java.io.IOException;

/**
 *
 */
public class LogTest {
    public static void main(String[] args){
        String fileOutputPath = "testOutput";
        String filePath = "logFile";
        String[] processes = new String[]{"bank1","bank2","bank3","server"};
        try {
            new Log(filePath, fileOutputPath, processes).write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
