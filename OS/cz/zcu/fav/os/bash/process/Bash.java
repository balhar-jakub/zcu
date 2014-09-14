package cz.zcu.fav.os.bash.process;

import cz.zcu.fav.os.bash.FileUtil;
import cz.zcu.fav.os.bash.parser.Command;

import java.io.*;
import java.util.List;

/**
 * This class represents main process in the application. Every processes are descendants of some of the shells.
 * By descendants I mean that on logical level are considered being run in the bash. Bashes may be run inside the
 * other bashes.
 *
 * @author Jakub Balhar
 * @version 0.1
 * @since 2012
 */
public class Bash extends ProcessOs {

    private cz.zcu.fav.os.bash.ClassLoader classLoader;

    private Command fgCommand;
    private List<Command> bgCommands;

    private boolean nextCommand;

    /**
     * Standard constructor as with any other process. args, is and os is not being used in this case. parent
     * may be null then its the parent bash of whole system.
     *
     * @param args unused
     * @param is unused
     * @param os unused
     * @param pid id of the bash
     * @param parent parent of the bash. May be null.
     */
    public Bash(String[] args, InputStream is, OutputStream os, Integer pid, ProcessOs parent) {
        this.pid = pid;
        this.parent = parent;
        name = this.getClass().getSimpleName();
    }

    /**
     * It is run when starting a thread. It waits dor commands to run and then runs them.
     */
    @Override
    public void runCommand() {
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
            if (nextCommand) {
                nextCommand = false;
                solveCommand(fgCommand, bgCommands);
            }
        }
    }

    /**
     * It sets flag nextCommand to true and in the next loop of main loop it runs command it received into the queue
     */
    public void runNextCommand() {
        nextCommand = true;
    }

    /**
     * It adds commands to be run to the bash.
     *
     * @param fgCommand Command to be run as foreground
     * @param bgCommands List of the piped commands
     * @param pis stdIn of the processes
     * @param pos stdOut of the processes
     */
    public void addCommand(Command fgCommand, List<Command> bgCommands,
                           PipedInputStream pis, PipedOutputStream pos) {
        this.fgCommand = fgCommand;
        this.bgCommands = bgCommands;
        stdIn = pis;
        stdOut = pos;
    }

    /**
     * It handles one foreground command and associated background commands. If there are any background commands
     * they are considered as piped and therefore new PipedInputStreams and OutputStreams are created to connect
     * them.
     *
     * @param foregroundCommand Command to be run as foreground
     * @param backgroundProcess Commands to be run as background, in this case piped.
     */
    private void solveCommand(Command foregroundCommand, List<Command> backgroundProcess) {
        PipedOutputStream lastPipedOutput;
        PipedInputStream nextPipedInput;

        Command lastBgCommand;
        if (backgroundProcess.size() > 0) {
            try {
                lastPipedOutput = new PipedOutputStream();
                nextPipedInput = new PipedInputStream(lastPipedOutput);

                runProcess(foregroundCommand, stdIn, lastPipedOutput);

                lastBgCommand = backgroundProcess.remove(backgroundProcess.size() - 1);
                for (Command command : backgroundProcess) {
                    lastPipedOutput = new PipedOutputStream();
                    runProcess(command, nextPipedInput, lastPipedOutput);
                    nextPipedInput = new PipedInputStream(lastPipedOutput);
                }

                runProcess(lastBgCommand, nextPipedInput, stdOut);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            runProcess(foregroundCommand, stdIn, stdOut);
        }
    }

    /**
     * It creates correct args array and runs the process. It also potentially changes the streams if
     * it was redirected to file or from file.
     *
     * @param command Command to be run
     * @param is Default InputStream
     * @param os Default OutputStream
     * @return Created process
     */
    private ProcessOs runProcess(Command command, InputStream is, OutputStream os) {
        String[] args = new String[command.getSwitches().length + command.getParams().length];
        System.arraycopy(command.getSwitches(), 0, args, 0, command.getSwitches().length);
        System.arraycopy(command.getParams(), 0, args, command.getSwitches().length, command.getParams().length);
        try {
            if (command.getInputFileDescriptor() != null) {
                String filePath = command.getInputFileDescriptor();
                filePath = FileUtil.getFullPath(filePath);
                is = new FileInputStream(new File(filePath));
            }
            if (command.getOutputFileDescriptor() != null) {
                os.close();
                String filePath =  command.getOutputFileDescriptor();
                filePath = FileUtil.getFullPath(filePath);
                File outputFile = new File(filePath);
                if(!outputFile.exists()){
                    outputFile.createNewFile();
                }
                os = new FileOutputStream(outputFile);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessOs process = classLoader.startProcess(command.getBinary(), args, is, os, this);
        return process;
    }

    /**
     * It is called when terminating the process.
     *
     * @return true
     */
    @Override
    public Boolean terminate() {
        this.interrupt();
        return true;
    }

    /**
     * Every Bash needs classLoader to be set before its first usage.
     *
     * @param classLoader
     */
    public void setClassLoader(cz.zcu.fav.os.bash.ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}