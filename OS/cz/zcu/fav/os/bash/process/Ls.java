package cz.zcu.fav.os.bash.process;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * Ls command class. It prints files in specific directory.
 *
 * @author Tomáš Prokop
 */
public class Ls extends ProcessOs {

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
    public Ls(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
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
        String[] fileNames;
        File file;
        if (args.length == 0) {
            file = new File(System.getProperty("user.dir"));
            fileNames = file.list();
            for (int i = 0; i < fileNames.length; i++) {
                ps.println(fileNames[i]);
            }
        } else {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) { //1. argument je -h nebo --help
                ps.println(help());
            } else if (args[0].equalsIgnoreCase("-l")) {
                File[] fileList;
                if (args.length == 1) {
                    file = new File(System.getProperty("user.dir"));
                    fileList = file.listFiles();
                    for (int j = 0; j < fileList.length; j++) {
                        printFormat("" + fileList[j].length(),
                                getTime(fileList[j].lastModified()),
                                fileList[j].getName());
                    }
                } else {
                    for (int i = 1; i < args.length; i++) {
                        if (args[i].equals("/")) {
                            file = new File(System.getProperty("user.home"));
                        } else if (args[i].equals("")) {
                            file = new File(System.getProperty("user.dir"));
                        } else {
                            file = new File(args[i]);
                        }
                        if (file.exists()) {
                            if (file.isDirectory()) {
                                fileList = file.listFiles();
                                ps.println(args[i] + ":");
                                for (int j = 0; j < fileList.length; j++) {
                                    printFormat("" + fileList[j].length(),
                                            getTime(fileList[j].lastModified()),
                                            fileList[j].getName());
                                }
                            } else {
                                ps.println(file.getName() + " not a directory.\n");
                            }
                        } else {
                            ps.println(file.getName() + " not such file or directory.\n");
                        }
                    }
                }
            } else {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("/")) {
                        file = new File(System.getProperty("user.home"));
                    } else if (args[i].equals("")) {
                        file = new File(System.getProperty("user.dir"));
                    } else {
                        file = new File(args[i]);
                    }
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            fileNames = file.list();
                            ps.println(args[i] + ":");
                            for (int j = 0; j < fileNames.length; j++) {
                                ps.println(fileNames[j]);
                            }
                        } else {
                            ps.println(file.getName() + " not a directory.\n");
                        }
                    } else {
                        ps.println(file.getName() + " not such file or directory.\n");
                    }
                }
            }
        }
    }

    /**
     * Method convert time in milisecond to standard time.
     *
     * @param time time in miliseconds
     * @return time as string in standard notation
     */
    private String getTime(Long time) {
        return new Date(time).toString();
    }

    /**
     * Method prints formated file attributs.
     *
     * @param size file size
     * @param mod time of last modification
     * @param name file name
     */
    private void printFormat(String size, String mod, String name) {
        String format = "%1$-15s%2$-30s%3$-35s\n";
        ps.format(format, size, mod, name);
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
     * ls command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Lists directories contents."
                + "\nSyntax: ls [OPTION] [PATH]..."
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits."
                + "\n\t -l\t\tDisplays file attributes."
                + "\n[PATH]:"
                + "\n\tPath to a directory.\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String[] argums = new String[2];
//        argums[0] = "build/classes/../a b/";
//        argums[0] = "";
        argums[0] = "-l";
        argums[1] = "c:\\aaa/filmy/";
//        argums[0] = "c:\\aaa/filmy/../Hry";
//        argums[0] = "build/classes/";
        Ls cat = new Ls(argums, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}