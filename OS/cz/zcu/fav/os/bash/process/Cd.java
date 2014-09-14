package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Cd command class. It changes actual working directory.
 *
 * @author Tomáš Prokop
 */
public class Cd extends ProcessOs {

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
    public Cd(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
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
        if (args.length == 0) { //bez argumentu
            ps.println("\n" + System.getProperty("user.dir") + "\n");
        } else { //help
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) { //1. argument je -h nebo --help
                ps.println(help());
            } else if (args[0].equals("/")) {
                System.setProperty("user.dir", System.getProperty("user.home"));
            } else { //
                File file;
                String path = convertPath(args[0]);

                if (path.equals("")) { //nic nezbylo = nic se neděje
                } else if (path.equals("..") || path.equals("../")) { // cd ..
                    file = new File(System.getProperty("user.dir")).getParentFile();
                    if (file != null) {
                        if (file.exists()) {
                            System.setProperty("user.dir", file.getAbsolutePath());
                        } else {
                            ps.println("No such directory.\n");
                        }
                    }
                } else if (!path.contains("..")) {//cd neobsahuje ..
                    path = FileUtil.getFullPath(path);
                    file = new File(path);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            System.setProperty("user.dir", file.getAbsolutePath());
                        } else {
                            ps.println("Not a directory.\n");
                        }
                    } else {
                        ps.println("No such directory.\n");
                    }
                } else { //jinak.. napr. cd ../dir/../../dir/dir
                    String[] parts = path.split("/");
                    if (parts[0].equals("..")) {
                        file = new File(System.getProperty("user.dir")).getParentFile();
                    } else {
                        if (parts[0].contains("\\..")) { //c:\../dir...
                            String filePath = FileUtil.getFullPath(parts[0].substring(0, 3));
                            file = new File(filePath);
                        } else {
                            String filePath = FileUtil.getFullPath(parts[0]);
                            file = new File(filePath);
                        }
                    }

                    if (file.exists()) {
                        for (int i = 1; i < parts.length; i++) {
                            if (parts[i].equals("..")) {
                                file = file.getParentFile();
                            } else if (parts[i].equals(".")) {
                                continue;
                            } else {
                                file = new File(file, parts[i]);
                            }
                        }
                    }

                    if (file != null && file.exists()) {
                        if (file.isDirectory()) {
                            System.setProperty("user.dir", file.getAbsolutePath());
                        } else {
                            ps.println("Not a directory.\n");
                        }
                    } else {
                        ps.println("No such directory.\n");
                    }
                }
            }
        }
    }

    /**
     * Method deletes ./ substrings in path to file/directory.
     *
     * @param path to file/directory
     * @return path without ./ substrings
     */
    private String convertPath(String path) {
        String[] p = path.split("/");
        path = "";

        //odstranění samostatných teček v cestě např dir/./dir2/. změní na dir/dir2/
        for (int i = 0; i < p.length; i++) {
            if (!".".equals(p[i])) {
                path += p[i] + "/";
            }
        }

        return path;
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
     * cd command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Change working directory."
                + "\nSyntax: cd [OPTION] [PATH]"
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits."
                + "\n[PATH]:"
                + "\n\tPath to the new working directory (eg. cd dir/dir2) or"
                + "\n\t.. change working directory to its parent directory (eg. cd ..) or"
                + "\n\tcombination (eg. cd dir/../../dir/).\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        //String[] argums = new String[1];
        //argums[0] = "build/classes/../a b/";
        //argums[0] = "c:\\aaa/filmy/";
        //argums[0] = "c:\\aaa/filmy/../Hry";
        //argums[0] = "build/classes/";
        //argums[0] = "c:\\../..";
        //argums[0] = "././././";
        //argums[0] = "/";
        //argums[0] = "build/./classes/./";
        Cd cat = new Cd(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}