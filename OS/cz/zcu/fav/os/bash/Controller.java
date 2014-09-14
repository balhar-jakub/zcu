package cz.zcu.fav.os.bash;

import cz.zcu.fav.os.bash.gui.TerminalWindow;
import cz.zcu.fav.os.bash.parser.Command;
import cz.zcu.fav.os.bash.parser.LexicalParser;
import cz.zcu.fav.os.bash.process.Bash;
import cz.zcu.fav.os.bash.process.ProcessOs;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is simple controller from MVC pattern. It handles communication between the GUI and the model.
 * It also handles input and output streams and their movement around the application. It always has one actual
 * foreground bash, which it gives commands to.
 *
 * @author Jakub Balhar
 * @version 0.1
 * @since 2012
 */
public class Controller extends Thread {
    private TerminalWindow window;
    private PipedOutputStream stdOut;
    private PipedInputStream stdIn;
    private Bash actualBash;
    private StringBuilder inputFromUser = new StringBuilder();

    private boolean isFinished;

    private boolean isProcessRunning = false;
    private boolean processOutClosed = true;
    private boolean handleCommands;

    private List<Command> fgQueue = new ArrayList<Command>();
    private List<List<Command>> bgQueue = new ArrayList<List<Command>>();

    private LexicalParser parser;
    private boolean writtenOnce = false;

    /**
     * It creates new lexical parser for its own purposes and sets created TerminalWindow.
     *
     * @param window TerminalWindow to communicate with.
     */
    public Controller(TerminalWindow window) {
        this.parser = new LexicalParser();
        this.window = window;
    }

    /**
     * It handles shortcut key. It calls associated action.
     *
     * @param shortcuts Shortcut which was called by user.
     */
    public void handleShortcut(Shortcuts shortcuts) {
        switch (shortcuts) {
            case CTRL_C:
                ProcessOs process = VirtualMachine.getForegroundProcess();
                if (process != null) {
                    process.terminate();
                }
                break;
            case CTRL_D:
                if(isProcessRunning()){
                    try {
                        stdOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    window.write("\n");
                    VirtualMachine.finishProcess(actualBash);
                }
                break;
        }
    }

    /**
     * It either writes data to the correct output stream. If there is any running process it writes data to
     * stream associated with this process. When there is no running process, it simply adds data to the
     * buffer. If the data is complete command it gives it to the parser.
     *
     * @param text Text to be added to buffer
     */
    public void addToStdInBuffer(String text) {
        if (isProcessRunning()) {
            try {
                text += "\n";
                stdOut.write(text.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            inputFromUser.append(text);
            if (parser.isCommandComplete(text)) {
                handleCommands = true;
                this.interrupt();
            }
        }
    }

    /**
     * It creates command based on the data in the input buffer and add commands to the foreground and background queue
     * If it is bash it does not create new input and output streams, otherwise it also creates Piped input and output
     * streams connected to the foreground process or in case of the piped processes to first process in pipe as stdIn
     * and to last process in the queue as stdOut.
     */
    private void handleCommands(){
        if(!addCommandsToQueue(inputFromUser.toString())){
            inputFromUser = new StringBuilder();
            return;
        }
        inputFromUser = new StringBuilder();

        Command foreground = fgQueue.remove(0);
        if(foreground.getBinary().equalsIgnoreCase("bash")){
            actualBash.addCommand(foreground, bgQueue.remove(0), null, null);
            actualBash.runNextCommand();
            actualBash.interrupt();
        } else {
            isProcessRunning = true;
            writtenOnce = true;
            actualBash.addCommand(foreground, bgQueue.remove(0), getStdIn(), getStdOut());
            actualBash.runNextCommand();
            actualBash.interrupt();
        }
    }

    /**
     * It gets list of the commands from the parser and then adds them to the queue . It also check if the commands
     * exists and if the file descriptors makes any sense.
     *
     * @param commandStr String containing valid commands.
     * @return false if either the command does not exist or if the file descriptors are wrong.
     */
    private boolean addCommandsToQueue(String commandStr) {
        Command[] commandsToCall = parser.parse(commandStr);
        List<Command> allCommands = new ArrayList<Command>();
        Collections.addAll(allCommands, commandsToCall);

        List<String> availableCommands = ClassLoader.getAvailableCommands();
        for (Command command : allCommands) {
            if(availableCommands.indexOf(command.getBinary()) == -1){
                window.write(command.getBinary() + " does not exist.\n");
                window.writeWDir();
                return false;
            }

            if(command.getInputFileDescriptor() != null){
                String filePath = command.getInputFileDescriptor();
                filePath = FileUtil.getFullPath(filePath);
                File inputFile = new File(filePath);
                if(!inputFile.exists()){
                    window.write(command.getInputFileDescriptor() + ": directory/file does not exist.\n");
                    window.writeWDir();
                    return false;
                }
            }

            if(command.getOutputFileDescriptor() != null){
                String filePath = command.getOutputFileDescriptor();
                filePath = FileUtil.getFullPath(filePath);
                File outputFile = new File(filePath);
                boolean wasCreated;
                try {
                    outputFile.delete();
                    wasCreated = outputFile.createNewFile();
                } catch (IOException e) {
                    wasCreated = false;
                }
                if(!wasCreated){
                    window.write(command.getOutputFileDescriptor() + ": directory/file does not exist.\n");
                    window.writeWDir();
                    return false;
                }
            }
        }

        List<Command> backgroundProcess = new ArrayList<Command>();
        for (Command command : allCommands) {
            if (command.getPrecedingDescriptor() != Command.Descriptor.PIPE) {
                fgQueue.add(command);

                backgroundProcess = new ArrayList<Command>();
                bgQueue.add(backgroundProcess);
            } else {
                backgroundProcess.add(command);
            }
        }
        return true;
    }

    /**
     * Main thread of the controller. It handles data from the application and sending commands to the bash
     * for starting them.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }

            if(isFinished){
                return;
            }

            if(handleCommands){
                handleCommands = false;
                handleCommands();
            }

            if(stdIn != null){
                handleIs();
            }

            if(!isProcessRunning() && writtenOnce){
                writtenOnce = false;
                if(fgQueue.size() > 0){
                    isProcessRunning = true;
                    writtenOnce = true;
                    actualBash.addCommand(fgQueue.remove(0), bgQueue.remove(0), getStdIn(), getStdOut());
                    actualBash.runNextCommand();
                } else {
                    window.writeWDir();
                }
            }
        }
    }

    /**
     * It reads data from the input stream associated with foreground process and writes them to the window.
     * When reading is finished it also sets processOutClosed to true.
     */
    private synchronized void handleIs(){
        int actualCharInt;
        try {
            while((actualCharInt = stdIn.read()) != -1){
                window.write(String.valueOf((char) actualCharInt));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        stdIn = null;
        try {
            stdOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        processOutClosed = true;
    }

    /**
     * It creates new associated PipedInputStream and PipeOutputStream and sets OutputStream as stdOut while
     * returning the input stream as input stream of the new process.
     *
     * @return newly created input stream.
     */
    private PipedInputStream getStdIn() {
        PipedInputStream processIn = new PipedInputStream();
        PipedOutputStream stdOut = null;
        try {
            stdOut = new PipedOutputStream(processIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stdOut = stdOut;
        return processIn;
    }

    /**
     * It creates new associated PipedInputStream and PipeOutputStream and sets InputStream as stdIn while
     * returning the output stream as output stream of the new process.
     *
     * @return newly created output stream.
     */
    private PipedOutputStream getStdOut() {
        PipedInputStream stdIn = new PipedInputStream();
        PipedOutputStream processOut = null;
        try {
            processOut = new PipedOutputStream(stdIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stdIn = stdIn;
        return processOut;
    }

    /**
     * It sets new actual bash and writes prompt.
     *
     * @param bash new actual bash
     */
    public void setActualBash(Bash bash) {
        if(this.actualBash != null){
            this.actualBash.background();
            window.writeWDir();
        }
        this.actualBash = bash;
    }

    /**
     * It returns whether process is running based on the isProcessRunning and processOutClosed
     *
     * @return true if foregroundProcess is running.
     */
    private boolean isProcessRunning(){
        return (isProcessRunning || !processOutClosed);
    }

    /**
     * It sets that VirtualMachine thinks that the foreground process is finished.
     */
    public synchronized void fgProcessFinished() {
        isProcessRunning = false;
        this.interrupt();
    }

    /**
     * It finished the main loop of this Thread.
     */
    public void finish() {
        isFinished = true;
    }
}