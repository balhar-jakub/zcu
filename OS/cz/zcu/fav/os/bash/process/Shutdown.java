package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.VirtualMachine;
import cz.zcu.fav.os.bash.process.Shutdown;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Shutdown command class.
 * It calls system method, that kills all running processes and shutdowns system.
 *
 * @author Tomáš Prokop
 */
public class Shutdown extends ProcessOs {

    private PrintStream ps;

    /**
     * Constructor
     * 
     * @param args program arguments
     * @param is input stream
     * @param pw output stream
     * @param pid process ID
     * @param parent parent process (mostly bash)
     */
    public Shutdown(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
        stdIn = is;
        stdOut = pw;
        this.parent = parent;
        this.pid = pid;
        this.name = this.getClass().getSimpleName();
    }

    @Override
    public void runCommand() {
        this.ps = new PrintStream(stdOut);
        VirtualMachine.shutdown();
    }

    @Override
    public Boolean terminate() {
        try {
            //ps.close();
            this.interrupt();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * shutdown command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Shuts down the system.\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Shutdown cat = new Shutdown(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}