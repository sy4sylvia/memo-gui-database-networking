package UI;
import Database.Connect;
import Networking.MemoData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

//UI that displays all memos stored in the database

public class MemoDisplayUI extends JFrame{
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newFeature;
    private JMenuItem exitFeature;

    private int memoNo;

    //top panel for textarea;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    private String resultTitle;

    //bottom panel for the textarea
    private JPanel bottomPanel;

    //textBox
    private JLabel selectLabel;
    private JTextField selectField;
    private JButton openButton;


    //no-arg constructor
    public MemoDisplayUI() {
        this.menuBar = new JMenuBar();
        this.fileMenu = new JMenu();
        this.newFeature = new JMenuItem();
        this.exitFeature = new JMenuItem();

        //file menu
        this.fileMenu.setText("File");
        //new
        newFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_MASK));
        newFeature.setText("New");
        newFeature.addActionListener(new NewActionListener());
        fileMenu.add(newFeature);
        //exit
        exitFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_MASK));
        exitFeature.setText("Exit");
        exitFeature.addActionListener((e) -> System.exit(0));
        fileMenu.add(exitFeature);
        fileMenu.add(exitFeature);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.textArea.setColumns(25);
        this.textArea.setRows(10);
        this.scrollPane.setViewportView(this.textArea);
        this.bottomPanel = new JPanel();

        this.selectLabel = new JLabel("Press Enter to Select a Memo: ");
        bottomPanel.add(selectLabel);

        this.selectField = new JTextField(10);
        selectField.addActionListener((e) -> System.out.println("textfield has value: " + selectField.getText()));
        bottomPanel.add(selectField);

        this.openButton = new JButton("Open");
        openButton.addActionListener(new OpenActionListener());
        bottomPanel.add(openButton);
        bottomPanel.setVisible(true);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.add(scrollPane);

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("All Memos");
        this.setVisible(true);
        this.setSize(500,400);
        this.setLocationRelativeTo(null);
    }

    public MemoDisplayUI(String sthInTextArea) {
        this.menuBar = new JMenuBar();
        this.fileMenu = new JMenu();
        this.newFeature = new JMenuItem();
        this.exitFeature = new JMenuItem();
        //file menu
        this.fileMenu.setText("File");
        //new
        newFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_MASK));
        newFeature.setText("New");
        newFeature.addActionListener(new NewActionListener());
        fileMenu.add(newFeature);
        //exit
        exitFeature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_MASK));
        exitFeature.setText("Exit");
        exitFeature.addActionListener((e) -> System.exit(0));

        fileMenu.add(exitFeature);
        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);

        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.textArea.setColumns(25);
        this.textArea.setRows(10);

        String sth = "WARNING: " + "\n" + "Server not connected." + "\n" + "Please start the server first to view all memos.";

        //default
        try {
            // Establish connection with the server
            Socket socket = new Socket("localhost", 8000);

            // Create an output stream to the server
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

            //create a memo subject and send to the server
            MemoData md = new MemoData(null, null);

            toServer.writeObject(md);
            toServer.flush();
            //try if the output stream of the server works fine
            // Create an input stream from the server
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

            try {
                Object titles = fromServer.readObject();
                sth = titles.toString();
            }catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

        textArea.setText(sth);

        this.scrollPane.setViewportView(this.textArea);
        this.bottomPanel = new JPanel();

        this.selectLabel = new JLabel("Press Enter to Select a Memo: ");
        bottomPanel.add(selectLabel);

        this.selectField = new JTextField(10);
        selectField.addActionListener((e) -> System.out.println("memo has title: " + selectField.getText()));
        bottomPanel.add(selectField);

        this.openButton = new JButton("Open");
        openButton.addActionListener(new OpenActionListener());
        bottomPanel.add(openButton);
        bottomPanel.setVisible(true);

        this.add(bottomPanel, BorderLayout.SOUTH);
        this.add(scrollPane);

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("All Memos");
        this.setVisible(true);
        this.setSize(500,300);
        this.setLocationRelativeTo(null);
    }

    private void setTextArea(String sth) {
        this.textArea = new JTextArea(sth);
    }

    class NewActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dispose();
            EditingUI editingUI = new EditingUI();
            editingUI.setVisible(true);
        }
    }

    class OpenActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resultTitle = selectField.getText();
            dispose();

            try {
                // Establish connection with the server
                Socket socket = new Socket("localhost", 8000);
                // Create an output stream to the server
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
                MemoData md = new MemoData(resultTitle);
                //this works just fine
                toServer.writeObject(md);
                toServer.flush();

                System.out.println(md);
                //server sends back the specified memo
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                try {
                    Object specifiedMemo = fromServer.readObject();
                    System.out.println("title and the contents? " + "\n" + specifiedMemo);

                    String[] xx = specifiedMemo.toString().split("\n", 2);
                    EditingUI editingUI = new EditingUI();
                    editingUI.setTitleField(xx[0]);
                    editingUI.setTextArea(xx[1]);
                    editingUI.setVisible(true);
                    editingUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                    JOptionPane.showMessageDialog(null, "No such memo");
                }
            }catch (IOException ioe) {
                ioe.printStackTrace();
                JOptionPane.showMessageDialog(null, "No such memo");
            }
        }
    }

    public static void main(String[] args) {
//        new MemoDisplayUI();
        new MemoDisplayUI("sthInTextArea");
    }
}
