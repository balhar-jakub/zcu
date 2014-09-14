package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.FileUtil;

import java.io.*;
import java.util.Scanner;

/**
 * Cat command class. It prints contents of input files.
 *
 * @author Tomáš Prokop
 */
public class Cat extends ProcessOs {

    private String[] args;
    private PrintStream ps;
    private static FileReader fr;
    private static BufferedReader bf;

    /**
     * Constructor
     *
     * @param args program arguments
     * @param is input stream
     * @param pw output stream
     * @param pid process ID
     * @param parent parent process (mostly bash)
     */
    public Cat(String[] args, InputStream is, OutputStream pw, Integer pid, ProcessOs parent) {
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
        if (args != null && args.length != 0) {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                ps.println(help());
            } else {
                File file;
                String line;
                String filePath;

                for (int i = 0; i < args.length; i++) {
                    try {
                        filePath = FileUtil.getFullPath(args[i]);
                        file = new File(filePath);

                        if (file.isDirectory()) {
                            ps.println("File " + args[i] + " is not a file.\n");
                            continue;
                        }

                        fr = new FileReader(file);
                        bf = new BufferedReader(fr);

                        while ((line = bf.readLine()) != null) {
                            ps.println(line);
                        }
                        bf.close();
                        fr.close();

                    } catch (FileNotFoundException ex) {
                        ps.println("File " + args[i] + " not found.\n");
                    } catch (IOException ioex) {
                        ps.println("Cannot read file.\n");
                    }
                }

            }
        } else {
            Scanner sc = new Scanner(stdIn);

            while (sc.hasNextLine()) {
                ps.println(sc.nextLine());
            }
        }
    }

    @Override
    public Boolean terminate() {
        try {
            bf.close();
            fr.close();
            this.interrupt();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * cat command manual
     *
     * @return string with manual
     */
    public static String help() {
        String h = "Concatenate FILE(s) or standard input to standard output."
                + "\nSyntax: cat [OPTION] [FILE]... "
                + "\n[OPTION]:"
                + "\n\t --help -h\tDisplays this help and exits."
                + "\nFILE]:"
                + "\n\tpath to a file\n";
        return h;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
//        argums = new String[2];
//        argums[0] = "--help";
//        argums[0] = "C:\\aaa/a.txt";
//        argums[1] = "build/a b/blbost.txt";
        Cat cat = new Cat(args, System.in, System.out, 0, null);
        cat.start();
        try {
            cat.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}