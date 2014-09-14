package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.VirtualMachine;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Kill command class.
 * It interupts running process with specific PID.
 *
 * @author Tomáš Prokop
 */
public class Kill extends ProcessOs {

    private String[] args;
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
    public Kill(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
        stdIn = is;
        stdOut = pw;
        this.parent = parent;
        this.pid = pid;
        this.args = args;
        this.name = this.getClass().getSimpleName();
    }

    @Override
    public void runCommand() {
        this.ps = new PrintStream(stdOut);
        if (args.length == 0) {
            ps.println(help());
        } else {
            int id;
            for (int i = 0; i < args.length; i++) {
                try {
                    id = Integer.valueOf(args[i]);
                    if (!VirtualMachine.killProcess(id)) {
                        ps.println("Process with PID " + args[i] + " does not exist.");
                    }
                } catch (NumberFormatException nfex) {
                    ps.println(args[i] + " is not a number.");
                }
            }
        }
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
     * kill command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Terminates a running process."
                + "\nSyntax: kill [PID]... "
                + "\n[PID]:"
                + "\n\tProcess ID\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Kill cat = new Kill(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}