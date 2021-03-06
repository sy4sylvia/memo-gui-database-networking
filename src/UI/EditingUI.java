package UI;

import Database.Connect;
import Networking.MemoData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.EmptyStackException;
import java.util.Stack;
import java.time.LocalDateTime;

//UI where multiple users can create and edit memos

public class EditingUI extends JFrame {

    private Stack<String> undoStack;
    private Stack<String> redoStack;
    private String helper;
    private boolean opened;
    private boolean saved;

    private JMenuBar menuBar;

    private JMenu fileMenu;
    private JMenuItem newFeature;
    private JMenuItem openFeature;
    private JMenuItem saveFeature;
    private JMenuItem exitFeature;

    private JMenu editMenu;
    private JMenuItem undoFeature;
    private JMenuItem redoFeature;
    private JMenuItem cutFeature;
    private JMenuItem copyFeature;
    private JMenuItem pasteFeature;
    private JMenuItem deleteSelectedFeature;
    private JMenuItem findFeature;

    private JMenu fontMenu;

    private JMenu sizeMenu;
    private JMenuItem biggerFeature;
    private JMenuItem smallerFeature;

    private JMenuItem styleMenu;
    private JCheckBoxMenuItem boldCheckBox;
    private JCheckBoxMenuItem italicCheckBox;

    private ActionListener listener;


    //font menu
    private String fontName;
    private int fontStyle = 0;
    private int fontSize;

    private int difference = 2;

    //top panel
    private JPanel topPanel;
    //textBox
    private JLabel titleLabel;
    private JTextField titleField;

    private JButton saveButton;

    private String resultTitle;

    //panel for the textarea
    private JScrollPane scrollPane;
    private JTextArea textArea;

    private Connection conn;
    private String name;
    private String allTexts;

    public EditingUI() {
        undoStack = new Stack<String>();
        redoStack = new Stack<String>();
        undoStack.push("");
        redoStack.push("");
        helper = "";
        opened = false;
        saved = false;
        this.initialComponents();

        //connecting to the database
        conn = Connect.connect();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public String getAllTexts() {
        allTexts = textArea.getText();
        return allTexts;
    }

    //initial components: method is called within the constructor to initialize the form
    private void initialComponents() {
        this.topPanel = new JPanel();

        this.titleLabel = new JLabel("Title (Press Enter to Save): ");
        topPanel.add(titleLabel);

        this.titleField = new JTextField(10);
        titleField.addActionListener((e) -> System.out.println("textfield has value: " + titleField.getText()));
        topPanel.add(titleField);

        createButton();
        topPanel.add(saveButton);
        topPanel.setVisible(true);

        this.add(topPanel, BorderLayout.NORTH);

        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.menuBar = new JMenuBar();

        this.fileMenu = new JMenu();
        this.newFeature = new JMenuItem();
        this.openFeature = new JMenuItem();
        this.saveFeature = new JMenuItem();
        this.exitFeature = new JMenuItem();

        this.editMenu = new JMenu();
        this.undoFeature = new JMenuItem();
        this.redoFeature = new JMenuItem();
        this.cutFeature = new JMenuItem();
        this.copyFeature = new JMenuItem();
        this.pasteFeature = new JMenuItem();
        this.deleteSelectedFeature = new JMenuItem();
        this.findFeature = new JMenuItem();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        System.out.println(formatDateTime);
        setTextArea("Created at: " + formatDateTime);

        fontSize = 13;
        fontStyle = Font.PLAIN;

        this.listener = new ChoiceListener();

        this.fontMenu = new JMenu();

        this.sizeMenu = new JMenu();
        this.smallerFeature = new JMenuItem();
        this.biggerFeature = new JMenuItem();

        this.styleMenu = new JMenu();
        this.boldCheckBox = new JCheckBoxMenuItem("Bold");
        this.italicCheckBox = new JCheckBoxMenuItem("Italic");

        this.setTitle("Memo");
        this.setAutoRequestFocus(false);
        this.textArea.setColumns(25);
        this.textArea.setRows(10);

        this.scrollPane.setViewportView(this.textArea);

        //file menu
        this.fileMenu.setText("File");
        //new
        newFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_MASK));
        newFeature.setText("New");
        newFeature.addActionListener(new NewActionListener());
        fileMenu.add(newFeature);
        //open
        openFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.META_MASK));
        openFeature.setText("Open");
        openFeature.addActionListener(new OpenActionListener());
        fileMenu.add(openFeature);
        //save current memo to the database: editedContents.db
        saveFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_MASK));
        saveFeature.setText("Save");
        saveFeature.addActionListener(new SaveActionListener());
        fileMenu.add(saveFeature);
        //exit
        exitFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_MASK));
        exitFeature.setText("Exit");
        exitFeature.addActionListener((e) -> System.exit(0));
        fileMenu.add(exitFeature);

        //editMenu
        this.editMenu.setText("Edit");
        //undo
        undoFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_MASK));
        undoFeature.setText("Undo Typing");
        undoFeature.addActionListener(new UndoActionListener());
        editMenu.add(undoFeature);
        //redo
        redoFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_MASK | InputEvent.SHIFT_MASK));
        redoFeature.setText("Redo Typing");
        redoFeature.addActionListener(new RedoActionListener());
        editMenu.add(redoFeature);
        //cut
        cutFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.META_MASK));
        cutFeature.setText("Cut");
        cutFeature.addActionListener(new CutActionListener());
        editMenu.add(cutFeature);
        //copy
        copyFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_MASK));
        copyFeature.setText("Copy");
        copyFeature.addActionListener(new CopyActionListener());
        editMenu.add(copyFeature);
        //paste
        pasteFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_MASK));
        pasteFeature.setText("Paste");
        pasteFeature.addActionListener(new PasteActionListener());
        editMenu.add(pasteFeature);
        //delete selected text
        deleteSelectedFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteSelectedFeature.setText("Delete");
        deleteSelectedFeature.addActionListener(new DeleteActionListener());
        editMenu.add(deleteSelectedFeature);
        //find
        findFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.META_MASK));
        findFeature.setText("Find");
        findFeature.addActionListener(new FindActionListener());
        editMenu.add(findFeature);

        //fontMenu
        this.fontMenu.setText("Font");

        //sub menu: size
        this.sizeMenu.setText("Size");
        biggerFeature.setText("Bigger");
        biggerFeature.addActionListener(new BiggerSizeListener());

        smallerFeature.setText("Smaller");
        smallerFeature.addActionListener(new SmallerSizeListener());

        sizeMenu.add(biggerFeature);
        sizeMenu.add(smallerFeature);
        fontMenu.add(sizeMenu);

        //sub menu: style
        this.styleMenu.setText("Style");
        boldCheckBox.addActionListener(listener);
        italicCheckBox.addActionListener(listener);

        styleMenu.add(boldCheckBox);
        styleMenu.add(italicCheckBox);

        fontMenu.add(styleMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(fontMenu);
        this.setJMenuBar(menuBar);

        this.add(scrollPane);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private boolean isOpened() {
        return opened;
    }

    private boolean isSaved() {
        return saved;
    }

    //save buttons
    private void createButton() {
        this.saveButton = new JButton("Confirm");
        saveButton.addActionListener(new SaveTitleActionListener());
    }

    class SaveTitleActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resultTitle = titleField.getText();
            setTitleField(resultTitle);
            JOptionPane.showMessageDialog(null, "Title Saved");
        }
    }

    class NewActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!isSaved()) {
                int choice = JOptionPane.showConfirmDialog(null, "Save current memo?", "File Not Saved", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) new SaveActionListener().actionPerformed(e);
                else if (choice == JOptionPane.CANCEL_OPTION) return;
            }
            dispose();
            EditingUI newUI = new EditingUI();
            newUI.setVisible(true);
        }
    }

    class SaveActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(null, "Save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION) return;
            else if (choice == JOptionPane.YES_OPTION) {
                try {
                    // Establish connection with the server
                    Socket socket = new Socket("localhost", 8000);

                    // Create an output stream to the server
                    ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

                    //create a memo subject and send to the server
                    //title = titleField.getText();
                    //contents = textArea.getText()
                    MemoData md = new MemoData(titleField.getText(), textArea.getText());

                    toServer.writeObject(md);
                    toServer.flush();

                    JOptionPane.showMessageDialog(null, "Memo Saved");

                    //try if the output stream of the server works fine
                    // Create an input stream from the server
                    ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

                    try {
                        Object titles = fromServer.readObject();
                        MemoDisplayUI mdui = new MemoDisplayUI(titles.toString());
                        mdui.setVisible(true);
                        mdui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        dispose();
                    }catch (ClassNotFoundException cnfe) {
                        cnfe.printStackTrace();
                    }
                }catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    class OpenActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!isSaved()) {
                int choice = JOptionPane.showConfirmDialog(null, "Save current memo?", "File Not Saved", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) new SaveActionListener().actionPerformed(e);
                else if (choice == JOptionPane.CANCEL_OPTION) return;
                else {
                    //Open without saving the current memo
                    try {
                        Socket socket = new Socket("localhost", 8000);
                        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

                        MemoData md = new MemoData(null, null);
                        toServer.writeObject(md);
                        toServer.flush();

                        try {
                            Object titles = fromServer.readObject();
                            MemoDisplayUI mdui = new MemoDisplayUI(titles.toString());
                            mdui.setVisible(true);
                        }catch (ClassNotFoundException cnfe) {
                            cnfe.printStackTrace();
                        }
                    }catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
            dispose();
        }
    }

    class UndoActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tmp;//change for backSpace and cut paste
            tmp = textArea.getText();
            redoStack.push(tmp);
            try {
                textArea.setText(undoStack.pop());
            } catch (EmptyStackException ese) {
                textArea.setText("Warning: previous input is empty, can't undo.");
            }
        }
    }

    class RedoActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tmp;
            tmp = textArea.getText();
            undoStack.push(tmp);
            try {
                textArea.setText(redoStack.pop());
            } catch (EmptyStackException ese) {
                textArea.setText("");
            }
        }
    }

    class CutActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tmp = textArea.getText();
            undoStack.push(tmp);
            helper = textArea.getSelectedText();
            textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
        }
    }

    class CopyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            helper = textArea.getSelectedText();
        }
    }

    class PasteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tmp = textArea.getText();
            undoStack.push(tmp);
            textArea.insert(helper, textArea.getCaretPosition());
        }
    }

    class DeleteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tmp = textArea.getText();
            undoStack.push(tmp);
            textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
        }
    }

    class FindActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JCheckBox ignoreCheckBox = new JCheckBox("Ignore Case");
            String message = " Enter: ";
            Object[] parameter = {ignoreCheckBox, message};
            boolean findStatus = true;

            String tmp = JOptionPane.showInputDialog(null, parameter, "Find", JOptionPane.DEFAULT_OPTION); //???
            String search = textArea.getText();
            if (ignoreCheckBox.isSelected()) {
                if (!search.toLowerCase().contains(tmp.toLowerCase())) {
                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "Not Found", "Unsuccessful Search", JOptionPane.ERROR_MESSAGE); //????
                }
                while (search.toLowerCase().contains(tmp.toLowerCase())) {
                    search = getString(tmp, search);
                }
                if (findStatus) {
//                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "No More Results Found", "Search Finished", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!search.contains(tmp)) {
                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "Not Found", "Unsuccessful Search", JOptionPane.ERROR_MESSAGE);
                }
                while (search.contains(tmp)) {
                    search = getString(tmp, search);
                }
                if (findStatus) {
//                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "No More Results Found", "Search Finished", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        private String getString(String tmp, String search) {
            int idx = search.toLowerCase().indexOf(tmp.toLowerCase());
            JOptionPane.showMessageDialog(null, "Found\nCursor is at the position", "Successful Search", JOptionPane.INFORMATION_MESSAGE);
            textArea.setCaretPosition(idx);
            StringBuilder replaceString = new StringBuilder();
            int i = 0;
            while (i < tmp.length()) {
                replaceString.append("~");
                i++;
            }
            search = search.substring(0, idx) + replaceString.toString() + search.substring(idx + tmp.length());
            return search;
        }
    }

    // This listener is shared among  =checkboxes
    class ChoiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setSampleFont();
        }
    }

    private void setSampleFont() {
        // Get font style
        int fontStyle = 0;
        if (italicCheckBox.isSelected()) fontStyle = fontStyle + Font.ITALIC;
        if (boldCheckBox.isSelected()) fontStyle = fontStyle + Font.BOLD;
        textArea.setFont(new Font(fontName, fontStyle, fontSize));
        textArea.repaint();
    }

    class BiggerSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fontSize += difference;
            setSampleFont();
        }
    }

    class SmallerSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fontSize -= difference;
            setSampleFont();
        }
    }

    public void setTitleField(String sth) {
        this.titleField.setText(sth);
    }

    public void setTextArea(String sth) {
        this.textArea.setText(sth);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditingUI curMemo = new EditingUI();
                curMemo.setVisible(true);
            }
        });
    }
}