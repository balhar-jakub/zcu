package cz.zcu.fav.os.bash.gui;

import java.io.File;

/**
 * History item.
 * Command and its working directory.
 *
 * @author Tomáš Prokop
 */
public class HistoryCommand {

    private final String line;
    private final File dir;

    /**
     * Constructor
     * 
     * @param line command text
     * @param dir command working directory
     */
    public HistoryCommand(String line, File dir) {
        this.line = line;
        this.dir = dir;
    }

    /**
     * Returns command working directory.
     *
     * @return working directory.
     */
    public File getDir() {
        return dir;
    }

    /**
     * Returns command.
     *
     * @return command
     */
    public String getLine() {
        return line;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HistoryCommand) {
            HistoryCommand com = (HistoryCommand) obj;
            if (com.getLine().equals(line) && com.getDir().getAbsolutePath().equals(dir.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.line != null ? this.line.hashCode() : 0);
        hash = 97 * hash + (this.dir != null ? this.dir.hashCode() : 0);
        return hash;
    }
}
