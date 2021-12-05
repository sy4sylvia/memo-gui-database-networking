package UI;

import Database.Connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class AllMemoUI extends JFrame {
    private JButton memo1;
    private JButton memo2;
    private JButton memo3;
    private JButton memo4;
    private JButton memo5;
    private JButton memo6;


    private JPanel panel;

    private JTextArea wordsBox; // results of the search go in here.

    //added:
    private JScrollPane scrollPane;
    private JMenuItem item1;
    private JMenuItem item2;
    private JLabel label;
    private JTextField textField;
    private JButton button;
    private Boolean fileLoaded = false;

    private static final int FRAME_WIDTH = 500;
    private static final int FRAME_HEIGHT = 225;


    private boolean saved;

    private Connection conn;


    public AllMemoUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.initialButtons();
        this.createButtonField();
        this.setSize(500,400);

        conn = Connect.connect();
    }

    private void initialButtons() {
        this.memo1 = new JButton("Memo 1:\nhere should be the title");
        this.memo2 = new JButton("Memo 2");
        this.memo3 = new JButton("Memo 3");
        this.memo4 = new JButton("Memo 4");
        this.memo5 = new JButton("Memo 5");
        this.memo6 = new JButton("Memo 6");

        memo1.addActionListener(new ButtonActionListener());
        memo2.addActionListener(new ButtonActionListener());
        memo3.addActionListener(new ButtonActionListener());
        memo4.addActionListener(new ButtonActionListener());
        memo5.addActionListener(new ButtonActionListener());
        memo6.addActionListener(new ButtonActionListener());

    }

    private void createButtonField() {
        this.panel = new JPanel(new GridLayout(3, 2));
        panel.add(memo1);
        panel.add(memo2);
        panel.add(memo3);
        panel.add(memo4);
        panel.add(memo5);
        panel.add(memo6);
        this.add(panel);
    }

    class ButtonActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            dispose();
            EditingUI eui = new EditingUI();
            eui.setVisible(true);
        }
    }


    public static void main(String[] args) {
        AllMemoUI m = new AllMemoUI();
        m.setVisible(true);
    }

}
