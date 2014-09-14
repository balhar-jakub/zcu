package cz.zcu.fav.os.bash.gui;

import cz.zcu.fav.os.bash.Controller;
import cz.zcu.fav.os.bash.Shortcuts;
import cz.zcu.fav.os.bash.VirtualMachine;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Terminal window - GUI and behavior.
 *
 * @author Tomáš Prokop
 */
public class TerminalWindow extends JFrame {

    private Document doc;
    private History history;
    private String line = "";
    private int startTextPosition;
    private Controller controller;
    //---------------------------------------------
    private JTextArea terminal;
    private JPanel panel;
    private JScrollPane scPane;
    //---------------------------------------------
    private String realFileSeparator = System.getProperty("file.separator");
    private File home = new File(System.getProperty("user.home"));
    private String[] fileNames;
    private int fileNamesIndex = 0;
    private String prefix = "";
    private String oldPrefix;
    private String part = "";
    private int insertTextPosition;
    //---------------------------------------------
    private final String FILE_SEPARATOR = "/";
    private final String PROMPT = ":-[";

    /**
     * Constructor. It only creates new command history. Terminal window runs
     * and shows method create.
     *
     * @see #create(java.lang.String)
     */
    public TerminalWindow() {
        history = new History(256);
    }

    /**
     * It creates a new terminal window and return it
     *
     * @param user user name
     * @return new Terminal Window
     */
    public static TerminalWindow create(final String user) {
        final TerminalWindow terminal = new TerminalWindow();
        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    terminal.initJFrame(user);
                }
            });
            return terminal;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * It initializes main window, adds listeners and components.
     *
     * @param user user name
     */
    private void initJFrame(String user) {
        this.setTitle(user);
        this.setSize(600, 700);
        initComponents();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                VirtualMachine.shutdown();
                super.windowClosing(e);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                if (terminal != null) {
                    terminal.getCaret().setVisible(true);
                }
                super.windowActivated(e);
            }
        });

        this.setVisible(true);
        doc = terminal.getDocument();

        writeWDir();
    }

    /**
     * Method writes a string into console.
     *
     * @param text text to be written into console.
     */
    public synchronized void write(final String text) {
        writeIn(text, -1, false, false, true);
    }

    /**
     * Method writes a string into document and initializes some global
     * variables.
     *
     * @param text text to be written into document.
     * @param position if -1 append text, else insert text at this position in
     * document
     * @param update update line start and prefix start?
     * @param update_prefix update prefix string?
     * @param external_write true when other process writes into window.
     */
    private synchronized void writeIn(final String text, final int position, final boolean update, final boolean update_prefix,
            final boolean external_write) {
        SwingUtilities.invokeLater(new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (position == -1) {
                    try {
                        doc.insertString(doc.getLength(), text, null);
                        terminal.setCaretPosition(doc.getLength());
                        if (external_write) {
                            startTextPosition = doc.getLength();
                            line = "";
                        }
                    } catch (BadLocationException ex) {
                    }
                } else {
                    try {
                        doc.insertString(position, text, null);
                        terminal.setCaretPosition(position + text.length());
                    } catch (BadLocationException ex) {
                    }
                }
                if (update) {
                    startTextPosition = doc.getLength();
                    insertTextPosition = startTextPosition;
                }

                updateLine();

                if (update_prefix) {
                    updatePrefix();
                }
                return null;
            }
        });
    }

    /**
     * Listing in command history from actual command to oldest one. If there is
     * no older commnad do nothing.
     */
    public synchronized void undo() {
        HistoryCommand com = history.undo();
        if (com != null) {
            insertCommand(com.getLine());
        }
    }

    /**
     * Listing in command history from actual command to newest one. If there is
     * no newer command do nothing.
     */
    public synchronized void redo() {
        HistoryCommand com = history.redo();
        if (com != null) {
            insertCommand(com.getLine());
        }
    }

    /**
     * Sets new controller to terminal window
     *
     * @param controller controller
     * @see Controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Returns actual working directory.
     *
     * @return actual working directory.
     */
    public File getWDir() {
        return new File(System.getProperty("user.dir"));
    }

    /**
     * Sets working directory to home directory.
     */
    public void goHome() {
        System.setProperty("user.dir", home.getAbsolutePath());
    }

    /**
     * Writes prompt.
     */
    public synchronized void writeWDir() {
        writeIn(replaceSpaces(changeSlashes(getWDir().getAbsolutePath())) + PROMPT, -1, true, false, false);
    }

    /**
     * It writes command from history into console
     *
     * @param com command
     */
    private void insertCommand(String com) {
        try {
            doc.remove(startTextPosition, doc.getLength() - startTextPosition);
            writeIn(com, -1, false, false, false);
        } catch (BadLocationException ex) {
            terminalError("A document error occurred.");
        }
    }

    /**
     * It writes part of path after tab is pressed.
     *
     * @param path part of path to file or directory
     */
    private void insertPart(String path) {
        try {
            doc.remove(insertTextPosition, doc.getLength() - insertTextPosition);
            writeIn(path, -1, false, false, false);
        } catch (BadLocationException ex) {
            terminalError("A document error occurred.");
        }
    }

    /**
     * It updates text of line
     */
    private void updateLine() {
        try {
            line = doc.getText(startTextPosition, doc.getLength() - startTextPosition);
        } catch (BadLocationException ex) {
            terminalError("A document error occurred.");
        }
    }

    /**
     * It updates text of prefix
     */
    private void updatePrefix() {
        updateLine();
        if (line.contains(" ")) {
            int index = line.lastIndexOf(' ') + 1;
            prefix = line.substring(index);
            insertTextPosition = startTextPosition + index;
        } else {
            prefix = line;
            insertTextPosition = startTextPosition;
        }
    }

    /**
     * It shows JOptionPane with error and cleans text in terminal.
     */
    private void terminalError(String errorMsg) {
        JOptionPane.showInternalMessageDialog(null, errorMsg,
                "Terminal error", JOptionPane.ERROR_MESSAGE);
        clean();
    }

    /**
     * It initializes components in main JFrame.
     */
    private void initComponents() {
        panel = new JPanel(new GridLayout(1, 1));
        scPane = new JScrollPane();
        terminal = new JTextArea();

        terminal.setBackground(Color.black);
        terminal.setFont(new Font("Monospaced", Font.PLAIN, 12));
        terminal.setForeground(Color.white);
        terminal.setAutoscrolls(true);
        terminal.setEditable(false);
        terminal.setFocusTraversalKeysEnabled(false);
        terminal.setLineWrap(true);
        terminal.setColumns(100);
        terminal.setCaretColor(Color.WHITE);
        terminal.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    e.consume();
                    enterTyped();
                } else if (e.getKeyChar() == KeyEvent.VK_TAB) {
                    e.consume();
                    tabTyped();
                } else if (e.getKeyChar() > 31 && e.getKeyChar() < 127) {
                    otherKeyTyped(e);
                    e.consume();
                } else {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    e.consume();
                    undo();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.consume();
                    redo();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    backspacePressed();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
                    e.consume();
                    ctrl_cPressed();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D) {
                    e.consume();
                    ctrl_dPressed();
                }
            }
        });

        terminal.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if (e.getDot() < startTextPosition) {
                    if (startTextPosition < doc.getLength()) {
                        terminal.getCaret().setDot(startTextPosition);
                    } else {
                        terminal.getCaret().setDot(doc.getLength());
                    }
                }
            }
        });

        scPane.setViewportView(terminal);
        panel.add(scPane);
        this.add(panel);
    }

    /**
     * Action after tab is typed.
     */
    private void tabTyped() {
        if (oldPrefix == null || !oldPrefix.equals(prefix)) {
            part = getFileList(prefix);
            fileNamesIndex = 0;
            oldPrefix = prefix;
            if (fileNames != null && fileNames.length != 0) {
                insertPart(part + replaceSpaces(fileNames[fileNamesIndex]));
                if (fileNamesIndex == fileNames.length - 1) {
                    fileNamesIndex = 0;
                } else {
                    fileNamesIndex++;
                }
            }
        } else {
            if (fileNames != null && fileNames.length != 0) {
                part = getFileList(prefix);
                if (fileNamesIndex >= fileNames.length) {
                    fileNamesIndex = 0;
                }
                insertPart(part + replaceSpaces(fileNames[fileNamesIndex]));
                if (fileNamesIndex == fileNames.length - 1) {
                    fileNamesIndex = 0;
                } else {
                    fileNamesIndex++;
                }
            }
        }
    }

    /**
     * It finds files with path, that matches prefix.
     *
     * @param pref prefix - part of path to file/directory
     * @return part of path to file/directory
     */
    private String getFileList(String pref) {
        if (pref.equals("")) {
            fileNames = getWDir().list();
            return "";
        }

        pref = replaceBackSlashes(pref);

        File file;
        String path;
        String retString;

        if (pref.length() > 2) {
            if (pref.charAt(1) != ':') {
                path = changeSlashes(getWDir().getAbsolutePath()) + FILE_SEPARATOR + pref;
            } else {
                pref = changeFirstSlash(pref, false);
                path = pref;
            }
        } else {
            path = changeSlashes(getWDir().getAbsolutePath()) + FILE_SEPARATOR + pref;
        }

        String[] pars = pref.split(FILE_SEPARATOR);

        file = new File(path);

        if (file.exists()) { //prefix is a path to a file or a directory
            if (file.isFile()) { //prefix is a file name
                fileNames = new String[1];
                fileNames[0] = pref;
                retString = "";
            } else { //prefix is a directory name
                fileNames = file.list();
                retString = replaceSpaces(changeFirstSlash(pref, true));
                if (retString.charAt(retString.length() - 1) != '/') {
                    retString += FILE_SEPARATOR;
                }
            }
        } else { //maybe first part of prefix is an existing path and second is prefix
            if (pars.length == 1 && pref.contains("\\")) { //eg. c:\Prog
                path = pref.substring(0, 3);
                file = new File(path);
                fileNames = file.list(new FileNameFilter(pref.substring(3)));
                retString = changeFirstSlash(path, true);
            } else if (pars.length == 1) { //it's prefix of file name from actual working directory
                fileNames = getWDir().list(new FileNameFilter(pref));
                retString = "";
            } else {//part of prefix is a path from actual working directory and part is prefix of file name eg. program\files/Gam
                path = path.substring(0, path.lastIndexOf(FILE_SEPARATOR));
                file = new File(path);
                if (file.exists()) {
                    if (file.isFile()) { //first part of prefix is file
                        fileNames = new String[1];
                        fileNames[0] = "";
                        retString = "";
                    } else { //first part of prefix is directory
                        fileNames = file.list(new FileNameFilter(pars[pars.length - 1]));
                        retString = replaceSpaces(pref.substring(0, pref.lastIndexOf(FILE_SEPARATOR)) + FILE_SEPARATOR);
                    }
                } else {
                    fileNames = new String[1];
                    fileNames[0] = "";
                    retString = "";
                }
            }
        }
        return retString;
    }

    /**
     * It replaces spaces for backslashes in input path.
     *
     * @param path path to file
     * @return path without spaces
     */
    private String replaceSpaces(String path) {
        return path.replace(' ', '\\');
    }

    /**
     * It replaces backslashes for spaces in input path.
     *
     * @param path path to file
     * @return path without backslashes
     */
    private String replaceBackSlashes(String path) {
        if (realFileSeparator.equals("\\")) {
            return path.replace('\\', ' ');
        }
        return path;
    }

    /**
     * It replaces backslashes for slashes in input path.
     *
     * @param path path to file
     * @return path with replaced backslashes
     */
    private String changeSlashes(String path) {
        if (realFileSeparator.equals("\\")) {
            return path.replace('\\', '/');
        }
        return path;
    }

    /**
     * It replaces first slash for backslash if program runs on windows and path
     * starts with e.g c:/.
     *
     * @param path path to file
     * @return path with changed first slash
     */
    private String changeFirstSlash(String path, boolean isBackSlash) {
        if (realFileSeparator.equals("\\") && path.length() > 2) {
            if (isBackSlash) {
                if (path.charAt(1) == ':') {
                    return path.substring(0, 2) + FILE_SEPARATOR + path.substring(3);
                }
            } else {
                if (path.charAt(1) == ':') {
                    return path.substring(0, 2) + "\\" + path.substring(3);
                }
            }
        }
        return path;
    }

    /**
     * This class is provided to filter file names.
     *
     * @see FilenameFilter
     */
    private class FileNameFilter implements FilenameFilter {

        private String prefix;

        public FileNameFilter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean accept(File dir, String name) {
            boolean fileOK = true;

            if (name != null) {
                fileOK &= name.startsWith(prefix);
            }

            return fileOK;
        }
    }

    /**
     * Action after CTRL+C is pressed
     */
    private void ctrl_cPressed() {
        controller.handleShortcut(Shortcuts.CTRL_C);
    }

    /**
     * Action after CTRL+D is pressed
     */
    private void ctrl_dPressed() {
        controller.handleShortcut(Shortcuts.CTRL_D);
    }

    /**
     * Action after backspace is pressed.
     */
    private void backspacePressed() {
        int pos = terminal.getCaretPosition();
        if (startTextPosition < doc.getLength()
                && startTextPosition < pos) {
            try {
                doc.remove(pos - 1, 1);
                updateLine();
                updatePrefix();
            } catch (BadLocationException ex) {
                terminalError("A document error occurred.");
            }
        }
    }

    /**
     * Action after enter is typed
     */
    private void enterTyped() {
        writeIn("\n", -1, true, false, false);
        prefix = "";

        if (!"".equals(line)) {
            try {
                if (doc.getText(startTextPosition - 3, 3).equals(PROMPT)) {
                    history.addToHistory(line, getWDir());
                }

                controller.addToStdInBuffer(line);
            } catch (BadLocationException ex) {
                System.out.println("terminal error");
            }
        } else {
            writeWDir();
        }
        line = "";
    }

    /**
     * Action after other relevant key is typed
     */
    private void otherKeyTyped(KeyEvent evt) {
        char c = evt.getKeyChar();
        writeIn("" + c, terminal.getCaretPosition(), false, true, false);
        updateLine();
        updatePrefix();
        if (c == KeyEvent.VK_SPACE) {
            prefix = "";

        }
    }

    /**
     * Cleans console window
     */
    private void clean() {
        try {
            doc.remove(0, doc.getLength());
            writeWDir();
            line = "";
            prefix = "";
        } catch (BadLocationException ex) {
            writeIn("A document error occurred.", -1, false, false, false);
            enterTyped();
        }
    }
}