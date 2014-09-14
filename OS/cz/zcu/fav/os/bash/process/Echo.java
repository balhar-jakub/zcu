package cz.zcu.fav.os.bash.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Echo command class.
 * It prints input arguments.
 *
 * @author Tomáš Prokop
 */
public class Echo extends ProcessOs {

    private PrintStream ps;
    private String[] args;

    /**
     * Constructor
     * 
     * @param args program arguments
     * @param is input stream
     * @param pw output stream
     * @param pid process ID
     * @param parent parent process (mostly bash)
     */
    public Echo(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
        stdIn = is;
        stdOut = pw;
        this.pid = pid;
        this.args = args;
        this.parent = parent;
        this.name = this.getClass().getSimpleName();
    }

    @Override
    public void runCommand() {
        this.ps = new PrintStream(stdOut);
        if (args.length == 0) {
        } else {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                ps.println(help());
            } else {
                try {
                    for (int i = 0; i < args.length; i++) {
                        ps.println(args[i]);
                    }
                } catch (Exception ex) {
                    ps.println("Cannot write a string.\n");
                }
            }
        }
    }

    @Override
    public Boolean terminate() {
        try {
            stdOut.flush();
            stdOut.close();
            this.interrupt();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * echo command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Echo the STRING(s) to the standard output."
                + "\nSyntax: Echo [OPTION] [STRING]... "
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits."
                + "\n[STRING]:"
                + "\n\tText to print.\n)";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
//        argums = new String[2];
//        argums[0] = "Ahoj světe!";
//        argums[1] = "Jak se máš?";
        Echo cat = new Echo(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}