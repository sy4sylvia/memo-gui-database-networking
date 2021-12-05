package UI;

import Database.Connect;

import javax.swing.*;
import java.awt.Font;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.EmptyStackException;
import java.util.Locale;
import java.util.Stack;
import java.time.LocalDateTime;


public class EditingUI extends JFrame {


    private Stack<String> undoStack;
    private Stack<String> redoStack;
    private String helper;
    // no open / save
//    private String filePath;
    //where should i choose the path to store the memos? on the desktop?? store in the databases. How
//should implement autosave to the database

    //menus...
//    private JPopupMenu popupMenu1;
//    private JMenu themeMenu;
    private JMenuBar menuBar;

    private JMenu fileMenu;
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
//    private JMenuItem boldFeature;
//    private JMenuItem italicFeature;

    private JCheckBoxMenuItem boldCheckBox;
    private JCheckBoxMenuItem italicCheckBox;
//    private JCheckBoxMenuItem underlineCheckBox;

    private ActionListener listener;
//    private JMenuItem underlineFeature;


    //font menu


    private String name;
    private int style;
    private int size;

    private int difference = 2;

    private JScrollPane scrollPane;
    private JTextArea textArea;

    private Connection conn;


    public EditingUI() {
        undoStack = new Stack<String>();
        redoStack = new Stack<String>();
        undoStack.push("");
        redoStack.push("");
        helper = "";
        this.initialComponents();
        conn = Connect.connect();
    }


    //initial components: method is called within the constructor to initialize the form
    private void initialComponents() {
//        this.popupMenu1 = new JPopupMenu();
//        this.themeMenu = new JMenu();

//        this.metalTheme = new JRadioButtonMenuItem();
//        this.nimbusTheme = new JRadioButtonMenuItem();
//        this.windowsClassicTheme = new JRadioButtonMenuItem();
//        this.windowsTheme = new JRadioButtonMenuItem(); //????

        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.menuBar = new JMenuBar();

        this.fileMenu = new JMenu();
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



//        this.name = name;
        name = textArea.getText();


        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        textArea.insert(formatDateTime, 0);

        size = 13;
        style = Font.PLAIN;

        this.listener = new ChoiceListener(); //???
//        this.style = style;
//        this.size = size;

        this.fontMenu = new JMenu();

        this.sizeMenu = new JMenu();
        this.smallerFeature = new JMenuItem();
        this.biggerFeature = new JMenuItem();

        this.styleMenu = new JMenu();
//        this.boldFeature = new JMenuItem();
//        this.italicFeature = new JMenuItem();

        this.boldCheckBox = new JCheckBoxMenuItem("Bold");
        this.italicCheckBox = new JCheckBoxMenuItem("Italic");
//        this.underlineCheckBox = new JCheckBoxMenuItem("Underline");


        //what is a separator????????????


//        this.themeMenu.setText("Themes");
        this.setTitle("Memo");
        this.setAutoRequestFocus(false);//？？？
        this.textArea.setColumns(25);
        this.textArea.setRows(10);

        this.scrollPane.setViewportView(this.textArea);


        //
        this.fileMenu.setText("File");
        //save current memo to the database: editedContents.db
        saveFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_MASK));
        saveFeature.setText("Save");
        saveFeature.addActionListener(new SaveActionListener());
        fileMenu.add(saveFeature);

        //exit to the ui of all memos
        exitFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_MASK));
        exitFeature.setText("Exit");
        exitFeature.addActionListener(new ExitActionListener());
        fileMenu.add(exitFeature);


        //editMenu
        this.editMenu.setText("Edit");

        //undo
        undoFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_MASK));
        undoFeature.setText("Undo");
        undoFeature.addActionListener(new UndoActionListener());
        editMenu.add(undoFeature);
        //redo
        redoFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.META_MASK));
        redoFeature.setText("Redo");
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

        biggerFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 2));
        biggerFeature.setText("Bigger");
        biggerFeature.addActionListener(new BiggerSizeListener());

        smallerFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 2));
        smallerFeature.setText("Smaller");
        smallerFeature.addActionListener(new SmallerSizeListener());


//        boldFeature.addActionListener(new BoldActionListener());
        sizeMenu.add(biggerFeature);
        sizeMenu.add(smallerFeature);
        fontMenu.add(sizeMenu);


        //sub menu: style
        this.styleMenu.setText("Style");
//        //bold
//        boldFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, 2));
//        boldFeature.setText("Bold");
//        boldFeature.addActionListener(new BoldActionListener());
////        fontMenu.add(boldFeature);
//        //italic
//        italicFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, 2));
//        italicFeature.setText("Italic");
//        italicFeature.addActionListener(new ItalicActionListener());
//        fontMenu.add(italicFeature);

//        styleMenu.add(boldFeature);
//        styleMenu.add(italicFeature);

        //bold
        boldCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, 2));
//        boldCheckBox.setText("Bold");
        boldCheckBox.addActionListener(listener);
//        boldCheckBox.addActionListener(new BoldActionListener());
//        fontMenu.add(boldFeature);
        //italic
        italicCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, 2));
//        italicCheckBox.setText("Italic");
//        italicCheckBox.addActionListener(new ItalicActionListener());
        italicCheckBox.addActionListener(listener);

        //underline
//        underlineCheckBox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, 2));
//        underlineCheckBox.addActionListener(listener);


        styleMenu.add(boldCheckBox);
        styleMenu.add(italicCheckBox);
//        styleMenu.add(underlineCheckBox);

        fontMenu.add(styleMenu);


        //layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
                                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
                                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                .addContainerGap())
        );


        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(fontMenu);
        this.setJMenuBar(menuBar);
        this.getContentPane().setLayout(layout); //maybe make layout local and private?

        pack();
    }

    class SaveActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String sql = "INSERT INTO memos(name,contents) VALUES(?,?)";


            //Connection conn = this.connect();
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);

                // set the corresponding param
                pstmt.setString(1, name);
                pstmt.setString(2, textArea.getText());
//                pstmt.setInt(3, id);

                // update
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Successfully Saved");
            } catch (SQLException sqlE) {
                sqlE.printStackTrace();
            }
        }
    }

    class ExitActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dispose();
            AllMemoUI ui = new AllMemoUI();
            ui.setVisible(true);
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

            String tmp = JOptionPane.showInputDialog(null, parameter , "Find",JOptionPane.DEFAULT_OPTION); //???
            String search = textArea.getText();
            if (ignoreCheckBox.isSelected()) {
                if (!search.toLowerCase().contains(tmp.toLowerCase())) {
                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "Not Found", "Unsuccessful Search", JOptionPane.ERROR_MESSAGE); //????
                }
                while (search.toLowerCase().contains(tmp.toLowerCase())) {
                    search = getString(tmp, search);
                }
                if(findStatus){
//                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "No More Results Found", "Search Finished", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else {
                if (!search.contains(tmp)){
                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "Not Found", "Unsuccessful Search", JOptionPane.ERROR_MESSAGE);
                }
                while (search.contains(tmp)){
                    search = getString(tmp, search);
                }
                if (findStatus) {
//                    findStatus = false;
                    JOptionPane.showMessageDialog(null, "No More Results Found", "Search Finished", JOptionPane.INFORMATION_MESSAGE);
                }
            }
//            System.out.println(tmp);
        }

        private String getString(String tmp, String search) {
            int idx = search.toLowerCase().indexOf(tmp.toLowerCase());
            JOptionPane.showMessageDialog(null, "Found\nCursor is at the position", "Successful Search", JOptionPane.INFORMATION_MESSAGE);
            textArea.setCaretPosition(idx);
            StringBuilder replaceString = new StringBuilder();
            int i = 0;
            while (i < tmp.length()){
                replaceString.append("~");
                i++;
            }
            search = search.substring(0, idx)+ replaceString.toString() + search.substring(idx + tmp.length());
            return search;
        }
    }



    // This listener is shared among  checkboxes
    class ChoiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            setSampleFont();
        }
    }




    private void setSampleFont() {
        // Get font style
        int style = 0;
        if (italicCheckBox.isSelected()) style = style + Font.ITALIC;
        if (boldCheckBox.isSelected()) style = style + Font.BOLD;
        textArea.setFont(new Font(name, style, size));
        textArea.repaint();
    }

    class BiggerSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            size += difference;
            setSampleFont();
        }
    }

    class SmallerSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            size -= difference;
            setSampleFont();
        }
    }


//    class SelectListener implements ActionListener {
//        public void actionPerformed(ActionEvent e) {
//            String tmp = textArea.getText();
//            undoStack.push(tmp);
//            textArea.insert(helper, textArea.getCaretPosition());
//        }
//    }



    class BoldActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
//            String faceName = textArea.getText();

            style = Font.BOLD;
//            int size = 13;
//            textArea.setFont(new Font(name, style, size));
//            textArea.repaint();
            setSampleFont();
        }
    }


    class ItalicActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
//            String faceName = textArea.getText();
            style = Font.ITALIC;
            setSampleFont();
//            textArea.setFont(new Font(name, style, size));
//            textArea.repaint();
        }
    }


    public static void main(String[] args) {
        /* Create and display the form */

//        new Runnable() {
//            public void run() {
//                new EditingUI().setVisible(true);
//            }
//        };
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditingUI().setVisible(true);
            }
        });
    }
}