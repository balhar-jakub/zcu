package cz.zcu.fav.os.bash.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tahle třída má zodpovědnost za zparsování celého příkazu. Na základě toho spustí pomocí ClassLoaderu odpovídající
 * procesy.
 * Každý z těchto procesů má jako parent proces aktuální bash. Jedinou výjimkou je pokud se objeví v parseru další bash
 * pak mají všechny následující jako parent process tento bash.
 * Process získáš z ClassLoaderu
 * public ProcessOs startProcess(String name, String[] args, InputStream is, PrintWriter pw, ProcessOs parent)
 * Pole args by mělo odpovídat poli, které by dostal příkaz od main.
 * Pak je také potřeba zavolat VirtualMachine.addProcess(process)
 *
 * Při vytváření Bashe je potřeba vytvořit nové is a ps, které mu jsou předány. Vytváří se následně, Bash si pak
 * při spuštění vytvoří druhou půlku.
 * PipedInputStream is = new PipedInputStream();
 * PipedOutputStream ps = new PipedOutputStream();
 *
 * V případě, že narazíš na Pipe, předej prvnímu procesu null jakožto OutputStream a pamatuj si jej.
 * Po startu na něm zavolej createPipeOutput.
 * Následně dalšímu procesu předej null jakožto InputStream a zavolej na něm metodu createPipeInput a předej
 * ji objekt z předchozího procesu.
 *
 * Pokud narazíš na && pak spusť první věc a zavolej na ní join. V ten okamžik tohle vlákno bude čekat dokud první příkaz
 * neskončí, pak pokračuj stejně jako standardně.
 *
 * Pokud je proces za pipeou nebo jde vyloženě o background process pak na něm nastav pomocí metody background(),
 * že jde o background proces.
 */
public class LexicalParser {

    public LexicalParser() {
    }

    /**
     * It parses an input string and creates an array with instances of the
     * Command class.
     * @param input command string
     * @return array of instances of Command class
     */
    public Command[] parse(String input) {
        input = input.trim();
        input = prepare(input);
        String[] commandsStr = input.split("&&|\\|");
        Command[] commands = new Command[commandsStr.length];
        String[] words = removeWhiteSpace(input.split(" "));
        String[] descriptors = new String[commandsStr.length - 1];
        int j = 0;  // descriptor index

        // just to be sure, trim all words so it doesn't start nor end with any
        // white space; save the descriptors (pipe or logical and)
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();

            if (words[i].equals("&&") || words[i].equals("|")) {
                descriptors[j] = words[i];
                j++;
            }
        }

        // create command objects and set preceding and succeeding descriptors
        for (int i = 0; i < commandsStr.length; i++) {
            Command command = new Command(commandsStr[i].trim().split(" "));
            commands[i] = command;

            if (i - 1 >= 0) {

                if (descriptors[i - 1].equals("&&")) {
                    command.setPrecedingDescriptor(Command.Descriptor.LOGICAL_AND);
                } else if (descriptors[i - 1].equals("|")) {
                    command.setPrecedingDescriptor(Command.Descriptor.PIPE);
                }
            }

            if (i < descriptors.length) {

                if (descriptors[i].equals("&&")) {
                    command.setSucceedingDescriptor(Command.Descriptor.LOGICAL_AND);
                }
                if (descriptors[i].equals("|")) {
                    command.setSucceedingDescriptor(Command.Descriptor.PIPE);
                }
            }
        }
        return commands;
    }

    /**
     * It tells us whether the command is complete or not. In general, command
     * can be split to more lines, so the end of the line is not automatically
     * end of a command.
     * @param input command string
     * @return true if it is complete, false if not
     */
    public boolean isCommandComplete(String input) {
        String[] words = input.split(" ");
        String last = words[words.length - 1].trim();

        // command cannot be complete if it ends with logical and or pipe
        if (last.equals("&&") || last.equals("|")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * It prepares the input string for parsing. It means that pipe always has
     * to be surrounded by spaces and file descriptors only preceded with a
     * space. If it isn't, this method corrects it.
     * @param input command string
     * @return input command string prepared for parsing
     */
    private String prepare(String input) {
        StringBuffer inputBuffer = new StringBuffer(input);
        // We have to make sure that all pipes are surrounded with spaces. Also,
        // file descriptors (<, >) must be attached to the file path (there must
        // be no space between descriptor and file path).
        boolean complete = false;
        boolean broken = false;

        while (!complete) {

            // go through the whole input
            for (int i = 0; i < inputBuffer.length(); i++) {

                if (inputBuffer.charAt(i) == '|') {

                    // pipe has to be surrounded with spaces, so add one before
                    // it (if it isn't already there)
                    if (i - 1 >= 0 && inputBuffer.charAt(i - 1) != ' ') {
                        inputBuffer.insert(i, " ");
                        broken = true;
                        break;
                    }
                    // and one after it
                    if (i + 1 < inputBuffer.length() && inputBuffer.charAt(i + 1) != ' ') {
                        inputBuffer.insert(i + 1, " ");
                        broken = true;
                        break;
                    }
                } else if (inputBuffer.charAt(i) == '>' || inputBuffer.charAt(i) == '<') {

                    // file descriptor has to be only preceded with a space, so
                    // add one before it (if it isn't already there)
                    if (i - 1 >= 0 && inputBuffer.charAt(i - 1) != ' ') {
                        inputBuffer.insert(i, " ");
                        broken = true;
                        break;
                    }

                    // and remove all succeeding spaces
                    if (i + 1 < inputBuffer.length() && inputBuffer.charAt(i + 1) == ' ') {
                        int whiteSpaceEnd = i + 1;
                        
                        while(inputBuffer.charAt(whiteSpaceEnd) == ' ') {
                            whiteSpaceEnd++;
                        }
                        
                        inputBuffer.delete(i + 1, whiteSpaceEnd);
                        broken = true;
                        break;
                    }
                }
            }
            // if the loop went without breaking to the end, it means that the
            // input string is ready for parsing
            if(!broken) {
                complete = true;
            } else {
                broken = false;
            }
        }
        input = inputBuffer.toString();
        return input;
    }
    
    /**
     * It removes all empty strings, which are result of parsing an input string
     * with multiple spaces.
     * @param words input string split to words
     * @return words without spaces
     */
    private static String[] removeWhiteSpace(String[] words) {
        boolean complete = false;
        boolean broken = false;
        List<String> wordsList = new ArrayList<String>(Arrays.asList(words));
        
        while(!complete) {

            for(int i = 0; i < wordsList.size(); i++) {
                
                if(wordsList.get(i).equals("")) {
                    wordsList.remove(i);
                    broken = true;
                    break;
                }
            }
            if(!broken) {
                complete = true;
            } else {
                broken = false;
            }
        }
        return wordsList.toArray(new String[wordsList.size()]);
    }
}