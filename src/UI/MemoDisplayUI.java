package UI;
import Database.Connect;
import Networking.MemoData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


public class MemoDisplayUI extends JFrame{
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


//    private Connection conn;
    private String name;
    private String allTexts;



    public MemoDisplayUI() {
        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.textArea.setColumns(25);
        this.textArea.setRows(10);
//        setTextArea("");
        this.scrollPane.setViewportView(this.textArea);
        this.bottomPanel = new JPanel();

        this.selectLabel = new JLabel("Press Enter to Select a Memo: ");
        bottomPanel.add(selectLabel);

        this.selectField = new JTextField(10);
        selectField.addActionListener((e) -> System.out.println("textfield has value: " + selectField.getText()));
        bottomPanel.add(selectField);

        this.openButton = new JButton("Open");
        openButton.addActionListener(new openActionListener()); //what to do here
        bottomPanel.add(openButton);
        bottomPanel.setVisible(true);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.add(scrollPane);

//        initComponents();

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(500,400);
        this.setLocationRelativeTo(null);

    }


    public MemoDisplayUI(String sthInTextArea) {
        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.textArea.setColumns(25);
        this.textArea.setRows(10);

        String sth = "WARNING: " + "\n" + "Server not connected." + "\n" + "Please start again to view all memos.";

        //default
        try {
            // Establish connection with the server
            Socket socket = new Socket("localhost", 8000);

            // Create an output stream to the server
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

            //create a memo subject and send to the server
            //title = titleField.getText();
            //contents = textArea.getText()
            MemoData md = new MemoData(null, null);

            toServer.writeObject(md);
            toServer.flush();
            //testing:
            System.out.println(toServer);

            //try if the output stream of the server works fine
            // Create an input stream from the server
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

            try {
                Object titles = fromServer.readObject();
                sth = titles.toString();
//                mdui.setVisible(true);
//                textArea.append("\n" +  "titles are: " + "\n" + titles.toString());
//                System.out.println(titles.toString());
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
        openButton.addActionListener(new openActionListener()); //what to do here
        bottomPanel.add(openButton);
        bottomPanel.setVisible(true);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.add(scrollPane);

//        initComponents();

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(500,400);
        this.setLocationRelativeTo(null);

    }

    private void setTextArea(String sth) {
        this.textArea = new JTextArea(sth);
    }


//    private void initComponents() {
//
//        this.scrollPane = new JScrollPane();
//        this.textArea = new JTextArea();
//
//        this.bottomPanel = new JPanel();
//        this.selectLabel = new JLabel("Select a memo: ");
//        bottomPanel.add(selectLabel);
//
//        this.selectField = new JTextField(10);
//        selectField.addActionListener(new selectActionListener());
//        bottomPanel.add(selectField);
//
//        this.selectButton = new JButton("Open");
////        selectButton.addActionListener(); //what to do here
//        bottomPanel.add(selectButton);
//        bottomPanel.setVisible(true);
//        this.add(bottomPanel, BorderLayout.SOUTH);
//
//    }

    class openActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            resultTitle = selectField.getText();

//            selectField.setText(selectField.getText());
//            System.out.println("textField has value: " + selectField);
            dispose();


            try {
                // Establish connection with the server
                Socket socket = new Socket("localhost", 8000);
//                System.out.println("--------------------------------");
//                System.out.println(resultTitle);
//                System.out.println("--------------------------------");
                // Create an output stream to the server
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
                MemoData md = new MemoData(resultTitle);
//                System.out.println("where am i --------------------------------");
//                System.out.println(md);
//                System.out.println("what ???--------------------------------");
                toServer.writeObject(md);
                toServer.flush();

                //server sends back the specified memo

                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

                try {
                    Object specifiedMemo = fromServer.readObject();
                    System.out.println(specifiedMemo);

//                    MemoData specifiedMD = (MemoData) specifiedMemo;
//                    String spTitle = specifiedMD.getName();
//                    String spContents = specifiedMD.getContents();
//                    System.out.println(spTitle);
//                    System.out.println(spContents);
                    EditingUI editingUI = new EditingUI();
                    editingUI.setVisible(true);

//                    sth = titles.toString();
//                mdui.setVisible(true);

                }catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                }
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }





        }
    }


    public static void main(String[] args) {
//        new MemoDisplayUI();
        new MemoDisplayUI("sthInTextArea");
    }

}
