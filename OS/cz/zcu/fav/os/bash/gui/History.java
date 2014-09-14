package cz.zcu.fav.os.bash.gui;

import java.io.File;
import java.util.ArrayList;

/**
 * Command history and add, undo, redo actions.
 *
 * @author Tomáš Prokop
 */
public class History {

    private final int SIZE;
    private ArrayList<HistoryCommand> history;
    private int position = 0;

    /**
     * Constructor
     *
     * @param size max number of saved commands in history
     */
    public History(int size) {
        this.SIZE = size;
        history = new ArrayList<HistoryCommand>();
    }

    /**
     * Adds new command to history.
     *
     * @param com new command
     */
    public void addToHistory(HistoryCommand com) {
        if (history.contains(com)) {
            history.remove(com);
        }
        if (history.size() >= SIZE) {
            history.remove(0);
        }
        history.add(com);
        position = history.size() - 1;
    }

    /**
     * Adds new command to history.
     *
     * @param com command text
     * @param dir command working dir
     */
    public void addToHistory(String com, File dir) {
        addToHistory(new HistoryCommand(com, dir));
    }

    /**
     * Returns older command if it's possible.
     *
     * @return command
     * @see HistoryCommand
     */
    public HistoryCommand undo() {
        HistoryCommand com = null;
        if (canUndo()) {
            com = history.get(position);
            if (position != 0) {
                position--;
            }
            return com;
        }
        return com;
    }

    /**
     * Returns newer command if it's possible.
     *
     * @return command
     * @see HistoryCommand
     */
    public HistoryCommand redo() {
        if (canRedo()) {
            position++;
            return history.get(position);
        }
        return null;
    }

    /**
     * Test if user can undo.
     *
     * @return true if can undo
     */
    private boolean canUndo() {
        if (history.isEmpty()) {
            return false;
        }
        return position >= 0;
    }

    /**
     * Test if user can redo.
     *
     * @return true if can redo
     */
    private boolean canRedo() {
        if (history.isEmpty()) {
            return false;
        }
        return position < history.size() - 1;

    }
}
