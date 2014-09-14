package cz.zcu.fav.os.bash;

import cz.zcu.fav.os.bash.gui.LoginDialog;
import cz.zcu.fav.os.bash.gui.TerminalWindow;
import cz.zcu.fav.os.bash.process.Bash;
import cz.zcu.fav.os.bash.process.ProcessOs;

import java.util.ArrayList;
import java.util.List;

/**
 * This is main class for the whole application. It is started when user starts
 * the application. It creates first BASH. This class also handles all processes
 * running at same time. It can return all processes running at the same time
 * and basically it can finish any process if necessary. This is usually used
 * only when ending the BASH. It is Singleton.
 *
 * @author Jakub Balhar
 * @version 0.1
 * @since 2012
 */
public class VirtualMachine {

    private static VirtualMachine vm;
    private static List<ProcessOs> processes = new ArrayList<ProcessOs>();
    private static int maxProcesses = 2048;
    private static boolean[] availableIds;
    private static Bash actualBash;
    private static Controller controller;
    private static String user = "temp_user";
    private static cz.zcu.fav.os.bash.ClassLoader classLoader = new cz.zcu.fav.os.bash.ClassLoader();
    private static final String BASH = "bash";

    /**
     * It sets availableIds as an array containing at most maxProcesses ids.
     */
    private VirtualMachine() {
        availableIds = new boolean[maxProcesses];
    }

    /**
     * Starting point of the application. It starts the whole application.
     *
     * @param args No arguments are expected.
     */
    public static void main(String[] args) {
        VirtualMachine vm = VirtualMachine.create();
        LoginDialog ld = new LoginDialog(vm);
        //vm.start();
    }

    /**
     * It returns user name.
     *
     * @return user name
     */
    public static String getUser() {
        return user;
    }

    /**
     * It sets user name.
     *
     * @param user name of the user
     */
    public static void setUser(String user) {
        VirtualMachine.user = user;
    }

    /**
     * It returns List of all running processes
     *
     * @return List of all actual processes
     */
    public static List<ProcessOs> getAllProcesses() {
        return processes;
    }

    /**
     * It creates the only instance of VirtualMachine, if there is none and
     * returns it. If it is called again, it returns the same VirtualMachine.
     *
     * @return VirtualMachine
     */
    public static VirtualMachine create() {
        if (vm == null) {
            vm = new VirtualMachine();
        }
        return vm;
    }

    /**
     * The whole machine can run at the same time at most 2048 processes. It
     * assigns ids of the processes from the first free id. If there is none
     * free id, it returns -1;
     *
     * @return Lowest available pid if some is free.
     */
    public static Integer getNewProcessId() {
        int newId = 0;
        while (availableIds[newId]) {
            newId++;
            if (newId >= maxProcesses) {
                throw new RuntimeException("Too many processes.");
            }
        }
        availableIds[newId] = true;
        return newId;
    }

    /**
     * It removes process from processes, if it is BASH which does not have
     * parent, finish all remaining processes and stop the application.
     * If it is bash with parent bash set it to the controller.
     *
     * @param process process to finish.
     */
    public static void finishProcess(ProcessOs process) {
        process.terminate();
        if(process.equals(getForegroundProcess())) {
            controller.fgProcessFinished();
        }
        processes.remove(process);
        availableIds[process.getPid()] = false;
        if (process.getCommandName().equalsIgnoreCase(BASH)) {
            if (process.getParent() == null) {
                for (ProcessOs processOs : processes) {
                    processOs.terminate();
                }
                controller.finish();
                System.exit(0);
            } else {
                actualBash = (Bash) process.getParent();
                actualBash.foreground();
                controller.setActualBash(actualBash);
            }
        }
    }

    /**
     * It adds processes to the running processes. If it is bash it also sets it to the controller.
     *
     * @param process process to add.
     */
    public static void addProcess(ProcessOs process) {
        processes.add(process);
        if (process.getCommandName().equalsIgnoreCase(BASH)) {
            actualBash = (Bash) process;
            controller.setActualBash(actualBash);
            actualBash.setClassLoader(classLoader);
        }
    }

    /**
     * It creates basic classes of the application and starts the first BASH.
     */
    public void start() {
        TerminalWindow window = TerminalWindow.create(user);

        controller = new Controller(window);
        // Create Bash
        window.setController(controller);

        actualBash = new Bash(new String[]{}, null, null, getNewProcessId(), null);
        VirtualMachine.addProcess(actualBash);
        actualBash.start();

        controller.start();
    }

    /**
     * It returns foreground process. At every moment there is at most one foreground process in the application.
     *
     * @return foregroundProcess or null.
     */
    public static ProcessOs getForegroundProcess() {
        for (ProcessOs process : processes) {
            if (!process.isBackground() && process.getParent() != null && process.getParent().equals(actualBash)) {
                return process;
            }
        }
        return null;
    }

    /**
     * It returns all first-level descendants of given process.
     *
     * @param bash parent process. Usually bash
     * @return List containing descendants. The list may be empty.
     */
    private static List<ProcessOs> descendantsOf(ProcessOs bash){
        List<ProcessOs> descendants = new ArrayList<ProcessOs>();
        for (ProcessOs process : processes) {
            if(process.getParent() == bash){
                descendants.add(process);
            }
        }
        return descendants;
    }

    /**
     * It kills single process. If this process is bash it also kills all processes running in the bash.
     * This works recursively, so if you kill bash containing other bash. It do the same for the descendant bash.
     *
     * @param pid Id fo the process to be killed
     * @return true if there was process with given id, false otherwise.
     */
    public static boolean killProcess(int pid) {
        for (ProcessOs process : processes) {
            if (process != null && process.getPid() == pid) {
                if(process.getCommandName().equalsIgnoreCase(BASH)){
                    List<ProcessOs> descendants = descendantsOf(process);
                    for(ProcessOs descendant: descendants){
                        if(!descendant.getCommandName().equalsIgnoreCase("kill")){
                            killProcess(descendant.getPid());
                        }
                    }
                }
                process.terminate();
                VirtualMachine.finishProcess(process);
                return true;
            }
        }
        return false;
    }

    /**
     * It finishes all remaining processes and then finishes the whole application.
     */
    public static void shutdown() {
        for (ProcessOs process : processes) {
            process.terminate();
        }
        System.exit(0);
    }
}