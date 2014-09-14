package cz.zcu.fav.os.bash.process;

import java.io.*;

/**
 * This is base class for every process run in application. It extends native java Thread, which
 * means that every command in application is being run as thread.
 * Every command must implement methods terminate and runCommand. It must be implemented
 * in subclasses, because the way they handles these differs.
 *
 * @author Jakub Balhar
 * @version 0.1
 * @since 2012
 */
public abstract class ProcessOs extends Thread {

    protected Integer pid;
    protected String name;
    protected String manual;
    protected Boolean background = false;
    protected ProcessOs parent;

    protected InputStream stdIn;
    protected OutputStream stdOut;

    /**
     * It is run when the process finishes. It is called by Bash. This function should be used to release
     * all currently used resources, if the process uses any.
     */
    public abstract Boolean terminate();

    /**
     * It is called when bash starts the process.It should contain the code for the process.
     */
    public abstract void runCommand();

    /**
     * It sends process to the background.
     */
    public void background() {
        background = true;
    }

    /**
     * It sets that this process is the foreground one. It is actually used only by the bash.
     */
    public void foreground(){
        background = false;
    }

    /* Simple getters */

    /**
     * If the process is run in background.
     *
     * @return If it is run in background, it returns true.
     */
    public Boolean isBackground() {
        return background;
    }

    /**
     * It returns id of this process.
     *
     * @return pid
     */
    public Integer getPid() {
        return pid;
    }

    /**
     * It returns name of the command.
     *
     * @return commandName
     */
    public String getCommandName() {
        name = (name == null) ? "" : name;
        return name;
    }

    /**
     * It returns process that spawned this process. In this case it always is Bash. If it is the first bash,
     * it returns null.
     *
     * @return parent of the process.
     */
    public ProcessOs getParent() {
        return parent;
    }

    @Override
    /**
     * It runs command in the new Threas and after finishing it, closes stdOut.
     */
    public void run() {
        runCommand();
        try {
            stdOut.flush();
            stdOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}