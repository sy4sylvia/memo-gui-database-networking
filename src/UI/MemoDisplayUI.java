package UI;
import Database.Connect;
import Networking.MemoData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

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
    private JButton selectButton;


//    private Connection conn;
    private String name;
    private String allTexts;



    public MemoDisplayUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);


        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.textArea.setColumns(25);
        this.textArea.setRows(10);

        this.scrollPane.setViewportView(this.textArea);

        //connect to the server, query for names of memos
        try {
            // Establish connection with the server
            Socket socket = new Socket("localhost", 8000);

            //create an input stream from server for titles of the memos
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
            Object allNames = fromServer.readObject();
//          System.out.println(allNames);

            String namesInString = (String) allNames;
            textArea.append(namesInString);

            String[] singleName = namesInString.split("\n");
            memoNo = singleName.length;

            for (int i = 0; i < singleName.length; i++) {
                System.out.println(singleName[i]);
                System.out.println("---------------------------------");
            }
            System.out.println(memoNo);


        }catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }




        this.bottomPanel = new JPanel();
        this.selectLabel = new JLabel("Press Enter to Select a Memo: ");
        bottomPanel.add(selectLabel);

        this.selectField = new JTextField(10);
        selectField.addActionListener((e) -> System.out.println("textfield has value: " + selectField));
        bottomPanel.add(selectField);

        this.selectButton = new JButton("Open");
//        selectButton.addActionListener(); //what to do here
        bottomPanel.add(selectButton);
        bottomPanel.setVisible(true);
        this.add(bottomPanel, BorderLayout.SOUTH);



        this.pack();
        this.setSize(500,400);
        this.setLocationRelativeTo(null);

    }



    private void initComponents() {

        this.scrollPane = new JScrollPane();
        this.textArea = new JTextArea();

        this.bottomPanel = new JPanel();
        this.selectLabel = new JLabel("Select a memo: ");
        bottomPanel.add(selectLabel);

        this.selectField = new JTextField(10);
        selectField.addActionListener((e) -> System.out.println("textfield has value: " + selectField));
        bottomPanel.add(selectField);

        this.selectButton = new JButton("Open");
//        selectButton.addActionListener(); //what to do here
        bottomPanel.add(selectButton);
        bottomPanel.setVisible(true);
        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MemoDisplayUI allMemo = new MemoDisplayUI();
                allMemo.setVisible(true);
            }
        });
    }

}
