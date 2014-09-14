package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.ClassLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * It gets list of available processes and for every of them asks for manual.
 * Add header and footer to it and then everything sends to output.
 *
 * @author Tomáš Prokop
 */
public class Man extends ProcessOs {

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
    public Man(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
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
            ps.println("Virtual operating system simulation.\n"
                    + "Implemented processes:\n"
                    + "\tcat, cd, echo, kill, ls, man, ps, pwd, shutdown, sort\n"
                    + "To run help for any program write: program_name -h (or --help)\n");
        } else {
            List<Class<? extends Object>> classes = ClassLoader.getAllProcesses();
            Method m;
            String result;
            boolean found = false;
            for (int i = 0; i < args.length; i++) {
                for (int j = 0; j < classes.size(); j++) {
                    try {
                        String cl = classes.get(j).getSimpleName();
                        if (cl.equalsIgnoreCase(args[i])) {
                            m = classes.get(j).getMethod("help");
                            result = (String) m.invoke(null);
                            ps.println(result);
                            found = true;
                        }
                    } catch (IllegalAccessException ex) {
                    } catch (IllegalArgumentException ex) {
                    } catch (InvocationTargetException ex) {
                    } catch (NoSuchMethodException ex) {
                    } catch (SecurityException ex) {
                    }
                }
                if (!found) {
                    ps.println("No manual entry for " + args[i]);
                }
                found = false;
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
     * man command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Displays manual."
                + "\nSyntax: man [PROGRAM] ..."
                + "\n[PROGRAM]:"
                + "\n\tName of a program eg. cat, ls.\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String[] a = new String[0];
        //a[0] = "-h";
        Man cat = new Man(a, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}