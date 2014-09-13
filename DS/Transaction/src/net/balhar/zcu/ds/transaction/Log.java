package net.balhar.zcu.ds.transaction;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class Log {
    private BufferedWriter log;

    public Log(File log) throws IOException {
        if(log.exists()) {
            log.delete();
        }
        log.createNewFile();
        this.log = new BufferedWriter(new FileWriter(log));
    }

    public void log(int srcTime, int distTime, String srcProcess, String distProcess, String data) {
        String toWrite = String.format("%s;%s;%s;%s;%s\n",
                srcProcess,
                String.valueOf(srcTime),
                distProcess,
                String.valueOf(distTime),
                data);
        try {
            log.write(toWrite);
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Logging did not work.");
        }
    }
}
