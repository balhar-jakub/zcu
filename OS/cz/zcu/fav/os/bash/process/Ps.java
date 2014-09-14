package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.VirtualMachine;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Ps command class.
 * It prints running processes.
 *
 * @author Tomáš Prokop
 */
public class Ps extends ProcessOs {

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
    public Ps(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
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
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                ps.println(help());
                return;
            }
        }
        List<ProcessOs> processes = VirtualMachine.getAllProcesses();
        ps.println("PID\tCommand");
        for (int i = processes.size() - 1; i >= 0; i--) {
            ps.println(processes.get(i).pid + "\t" + processes.get(i).name);
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
     * ps command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Prints all running processes."
                + "\nSyntax: ps [OPTION]"
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits.\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Ps cat = new Ps(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}