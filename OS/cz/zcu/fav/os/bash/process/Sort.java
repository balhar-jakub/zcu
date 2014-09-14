package cz.zcu.fav.os.bash.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/**
 * Sort command manual.
 * It sorts and prints input strings.
 * 
 * @author Tomáš Prokop
 */
public class Sort extends ProcessOs {

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
    public Sort(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
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
            } else {
                Arrays.sort(args);
                for (int i = 0; i < args.length; i++) {
                    ps.println(args[i]);
                }
                ps.println();
            }
        } else {
            Scanner sc = new Scanner(stdIn);
            ArrayList<String> lines = new ArrayList<String>();
            String line;

            while (sc.hasNextLine()) {
                line = sc.nextLine();

                if (line != null && !line.equals("")) {
                    lines.add(line);
                }
            }
            
            Collections.sort(lines);
            for (String tmp : lines) {
                ps.println(tmp);
            }
        }
    }

    @Override
    public Boolean terminate() {
        try {
            stdOut.close();
            this.interrupt();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * sort command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Sorts an input."
                + "\nSyntax: sort [OPTION] [STRING] ..."
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits."
                + "\n[STRING]:"
                + "\n\tInput.\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Sort cat = new Sort(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}