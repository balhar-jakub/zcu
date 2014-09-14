package cz.zcu.fav.os.bash;

import cz.zcu.fav.os.bash.process.ProcessOs;
import org.reflections.Reflections;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is used for getting new processes via reflection based on the name of the process.
 *
 * @author Jakub Balhar
 * @version 0.1
 * @since 2012
 */
public class ClassLoader {
    private ProcessOs process;
    private static List<String> availableCommands;

    /**
     * It creates new process and to the Constructor it passes args, is, os, pid and parent as arguments.
     *
     * @param name Name of the process.
     * @param args Arguments of the process
     * @param is InputStream for the process
     * @param os OutputStream for the process
     * @param parent Parent of the process. It may be null.
     * @return Newly created and started process.
     */
    public ProcessOs startProcess(String name, String[] args, InputStream is, OutputStream os, ProcessOs parent) {
        try {
            String rest = name.substring(1);
            String firstLetter = name.substring(0, 1);
            firstLetter = firstLetter.toUpperCase();
            Class loadedProgram = Class.forName("cz.zcu.fav.os.bash.process." + firstLetter + rest);
            Class[] params = new Class[5];
            params[0] = Class.forName("[Ljava.lang.String;");
            params[1] = InputStream.class;
            params[2] = OutputStream.class;
            params[3] = Integer.class;
            params[4] = ProcessOs.class;
            Constructor classConstructor = loadedProgram.getConstructor(params);
            Object object = classConstructor.newInstance(new Object[]{args, is, os,
                    VirtualMachine.getNewProcessId(), parent});
            process = (ProcessOs) object;
            VirtualMachine.addProcess(process);
            final ProcessOs p = runProcess();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        p.join();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    fireEvent(p);
                }
            }).start();
            return process;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * It returns all subclasses of the ProcessOs and therefore also all available commands. This method
     * uses Reflections library for getting all possible Commands.
     *
     * @return List of available commands as the classes.
     */
    public static List<Class<? extends Object>> getAllProcesses() {
        Reflections reflections = new Reflections("cz.zcu.fav.os.bash.process");

        Set<Class<? extends ProcessOs>> allClasses =
                reflections.getSubTypesOf(ProcessOs.class);
        List<Class<? extends Object>> allAvailableCommands = new ArrayList<Class<? extends Object>>();
        String commandName;
        for (Class<? extends Object> classCommand : allClasses) {
            commandName = classCommand.getSimpleName();
            if (commandName.equals("ProcessOs")) {
                continue;
            }
            allAvailableCommands.add(classCommand);
        }
        return allAvailableCommands;
    }

    /**
     * It returns list of available commands as String representations of their names.
     *
     * @return names of all available Strings.
     */
    public static List<String> getAvailableCommands(){
        if(availableCommands == null){
            availableCommands = new ArrayList<String>();
            List<Class<? extends Object>> available = getAllProcesses();
            for(Class<? extends Object> command: available){
                availableCommands.add(command.getSimpleName().toLowerCase());
            }
        }
        return availableCommands;
    }

    /**
     * It finishes process when its thread is finished.
     *
     * @param p process to be finished.
     */
    public void fireEvent(ProcessOs p) {
        VirtualMachine.finishProcess(p);
    }

    /**
     * It simply starts actual process if it is not null.
     *
     * @return process
     * @see ProcessOs
     */
    public ProcessOs runProcess() {
        if (process != null) {
            process.start();
            return process;
        }
        return null;
    }
}
