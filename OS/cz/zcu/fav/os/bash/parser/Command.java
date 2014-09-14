package cz.zcu.fav.os.bash.parser;

import java.util.Arrays;
import java.util.List;
import cz.zcu.fav.os.bash.ClassLoader;

public class Command {
    
    public enum Error {
        NONE,
        MISPLACED_ELEMENT,
        INVALID_BINARY_NAME;
    }
    
    public enum Descriptor {
        PIPE,
        LOGICAL_AND,
        NONE;
    }
    
    private String binary = null;
    private String[] params = new String[0];
    private String[] switches = new String[0];
    private String inputFileDescriptor = null;
    private String outputFileDescriptor = null;
    private Descriptor precedingDescriptor = Descriptor.NONE;
    private Descriptor succeedingDescriptor = Descriptor.NONE;
    private boolean inBackground = false;
    private Error error = Error.NONE;

    /**
     * Creates command from given words. Since preceding or succeeding
     * descriptors (pipe or logical AND) are not part of the input array, they
     * must be set additionaly by given setters.
     * @param words whole command split to single words
     */
    public Command(String[] words) {
        // tells us where to start next processing in the words array
        int index = 0;
        
        // if we processed all words already, stop parsing
        if(index >= words.length)
            return;
        // set the binary name so we know which process it is (cat, ls, ...)
        index = this.parseBinary(words, index);

        // if we processed all words already, stop parsing
        if(index >= words.length)
            return;
        // set all switches of the command
        index = this.parseSwitches(words, index);
        
        // if we processed all words already, stop parsing
        if(index >= words.length)
            return;
        // set all params of the command
        index = this.parseParams(words, index);
        
        // if we processed all words already, stop parsing
        if(index >= words.length)
            return;
        // set the file descriptors or process in background flag
        while(index < words.length) {
            index = this.parseDescriptors(words, index);
        }
    }
    
    /**
     * Sets binary name from the words array. If it's an invalid one, it sets
     * an error flag.
     * @param words whole command split to single words
     * @param index tells us where to start the processing in the words array
     * @return index in the words array where to start the next processing
     */
    private int parseBinary(String[] words, int index) {
        // get all processes that are implemented in the virtual machine
        List<Class<? extends Object>> classes = ClassLoader.getAllProcesses();
        // flag which is set to true only if we find out that the parsed binary
        // name is valid
        boolean valid = false;
        
        // go through all processes
        for (int j = 0; j < classes.size(); j++) {
            // get name of a process
            String name = classes.get(j).getSimpleName().toLowerCase();
            
            // compare it with a binary name from the command
            if (words[index].equals(name)) {
                // if it's valid, set the flag and don't continue with loop
                valid = true;
                break;
            }
        }
        
        // if the binary name is invalid (process doesn't exist), set the error
        // flag
        if(!valid) {
            this.error = Error.INVALID_BINARY_NAME;
        }
        // set the binary name (even if it's an invalid one)
        this.binary = words[index];
        // move on to the next word
        index++;
        return index;
    }
    
    /**
     * Sets switches which are processed from the words array.
     * @param words whole command split to single words
     * @param index tells us where to start the processing in the words array
     * @return index in the words array where to start the next processing
     */
    private int parseSwitches(String[] words, int index) {
        // array where we put switches found in the next process; there cannot
        // be more switches than number of words in the command, so the words
        // number is enough for the switches array length
        this.switches = new String[words.length];
        // words index
        int i = index;
        // switches index
        int j = 0;

        // go through the whole command; switches start witch either "-" or "--";
        // also, be careful about arrays length
        while(i < words.length && j < this.switches.length
                && words[i].startsWith("-")) {
            // copy the switch to the switches array
            this.switches[j] = words[i];
            // increment indices--move on to the next word in the command
            i++;
            j++;
        }
        
        // if there were no switches at all
        if(j == 0) {
            this.switches = new String[0];
        } else {
            // finally, trim the switches array
            this.switches = Arrays.copyOf(this.switches, j);
        }
        return i;
    }
    
    /**
     * Sets params which are processed from the words array.
     * @param words whole command split to single words
     * @param index tells us where to start the processing in the words array
     * @return index in the words array where to start the next processing
     */
    private int parseParams(String[] words, int index) {
        // array where we put params found in the next process; there cannot
        // be more params than number of words in the command, so the words
        // number is enough for the params array length
        this.params = new String[words.length];
        // words index
        int i = index;
        // params index
        int j = 0;
        
        // go through the whole command; param can be anything, but there cannot
        // be any more switches found; also, be careful about arrays length
        while(i < words.length && j < this.params.length) {
            
            // we got to the file descriptors or "launch in background" flag
            if(words[i].startsWith("<") || words[i].startsWith(">") ||
                    words[i].equals("&")) {
                // trim the params array and end params processing
                this.params = Arrays.copyOf(this.params, j);
                return i;
            }
            
            // there cannot be any switch now
            if(words[i].startsWith("-")) {
                // set the error flag
                this.error = Error.MISPLACED_ELEMENT;
                // don't save any params and don't continue the processing
                this.params = new String[0];
                return i;
            }
            
            // copy the param to the params array
            this.params[j] = words[i];
            // increment indices--move on to the next word in the command
            i++;
            j++;
        }
        // finally, trim the params array
        this.params = Arrays.copyOf(this.params, j);
        // move on to the next word
        i++;
        return i;
    }
    
    private int parseDescriptors (String[] words, int index) {
        // words index
        int i = index;
        
        // word is an input file descriptor
        if(words[index].startsWith(">")) {
            // set the descriptor as the file path only (without the ">" character)
            this.outputFileDescriptor = words[index+1].trim();
            i++;
        // word is an output file descriptor
        } else if(words[index].startsWith("<")) {
            // set the descriptor as the file path only (without the "<" character)
            this.inputFileDescriptor = words[index+1].trim();
            i++;
        // word is a background descriptor
        } else if(words[index].equals("&")) {
            // set the flag
            this.inBackground = true;
        // everything else here is an error
        } else {
            // set the error flag
            this.error = Error.MISPLACED_ELEMENT;
        }
        // move on to the next word
        i++;
        return i;
    }
    
    /**
     * Binary name getter.
     * @return binary name (even if it's an invalid one)
     */
    public String getBinary() {
        return this.binary;
    }
    
    /**
     * Params array getter.
     * @return params array
     */
    public String[] getParams() {
        return this.params;
    }
    
    /**
     * Switches array getter.
     * @return switches array
     */
    public String[] getSwitches() {
        return this.switches;
    }
    
    /**
     * Input file descriptor path getter.
     * @return input file descriptor path (without the "<" character)
     */
    public String getInputFileDescriptor() {
        return this.inputFileDescriptor;
    }
    
    /**
     * Output file descriptor path getter.
     * @return output file descriptor path (without the ">" character)
     */
    public String getOutputFileDescriptor() {
        return this.outputFileDescriptor;
    }
    
    /**
     * Preceding descriptor getter.
     * @return preceding descriptor (pipe, logical and, none)
     */
    public Descriptor getPrecedingDescriptor() {
        return this.precedingDescriptor;
    }
    
    /**
     * Succeeding descriptor getter.
     * @return succeeding descriptor (pipe, logical and, none)
     */
    public Descriptor getSucceedingDescriptor() {
        return this.succeedingDescriptor;
    }
    
    /**
     * Tells us whether the process runs in background or not.
     * @return true if the process runs in background, false if not
     */
    public boolean isInBackground() {
        return this.inBackground;
    }
    
    /**
     * Error getter. There can be two--misplaced element (like a switch after a
     * param) or invalid binary name.
     * @return error value (misplaced element, invalid binary name or none)
     */
    public Error getError() {
        return this.error;
    }
    
    /**
     * Preceding descriptor (pipe or logical and) setter. This must be used
     * additionally, because this descriptor is not a part of the input words
     * array.
     * @param precedingDescriptor pipe, logical and or none (default, no need
     * to set this value)
     */
    public void setPrecedingDescriptor(Descriptor precedingDescriptor) {
        this.precedingDescriptor = precedingDescriptor;
    }
    
    /**
     * Succeeding descriptor (pipe or logical and) setter. This must be used
     * additionally, because this descriptor is not a part of the input words
     * array.
     * @param succeedingDescriptor pipe, logical and or none (default, no need
     * to set this value)
     */
    public void setSucceedingDescriptor(Descriptor succeedingDescriptor) {
        this.succeedingDescriptor = succeedingDescriptor;
    }
}