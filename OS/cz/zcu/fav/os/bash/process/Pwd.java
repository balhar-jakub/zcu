package cz.zcu.fav.os.bash.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Pwd command class.
 * It prints actual working directory.
 *
 * @author Tomáš Prokop
 */
public class Pwd extends ProcessOs {

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
    public Pwd(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
        stdIn = is;
        stdOut = pw;
        this.args = args;
        this.parent = parent;
        this.pid = pid;
        this.name = this.getClass().getSimpleName();
    }

    @Override
    public void runCommand() {
        this.ps = new PrintStream(stdOut);
        if (args != null && args.length > 0) {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                ps.println(help());
            }
        } else {
            ps.println("\t" + System.getProperty("user.dir")+"\n");
        }
        //*************************
        ps.close();
        //*************************
    }

    @Override
    public Boolean terminate() {
        try {
            //ps.close();
            this.interrupt();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * pwd command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Prints a full filename of the current working directory."
                + "\nSyntax: pwd [OPTION]"
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits.\n";
        return h;
    }

    /**
     * Just test method.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Pwd cat = new Pwd(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}