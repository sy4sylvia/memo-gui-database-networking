package UI;

import Database.Connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveTitle extends JFrame {
//    JTextField textField;

    private JLabel titleLabel;
    private JTextField titleField;

    private JButton saveButton;
    private JButton cancelButton;

    private Connection conn;
    private String result;


    public SaveTitle() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        titleLabel = new JLabel("Press Enter to Save: ");
        this.add(titleLabel);
        //textfield
        titleField = new JTextField(10);
        setLayout(new FlowLayout());
        this.add(titleField);
        titleField.addActionListener((e) -> System.out.println("textfield has value: " + titleField.getText()));

        createButton();
        this.add(saveButton);
        this.add(cancelButton);
        this.pack();

        this.setSize(200, 160);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        conn = Connect.connect();
    }


    //save and cancel buttons
    private void createButton() {
        this.saveButton = new JButton("Confirm");
        this.cancelButton = new JButton("Cancel");
        saveButton.addActionListener(new SaveTitle.SaveTitleActionListener());
        cancelButton.addActionListener((e -> System.exit(0)));

    }
    class SaveTitleActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            result = titleField.getText();
            titleField.setText(titleField.getText());

            String sql = "INSERT INTO memos(name, contents) VALUES(?,?)";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                // set the corresponding param
                pstmt.setString(1, titleField.getText());
                System.out.println("--------\nthis is after inserting into the database");
                System.out.println(result); ///by this time, name has not be assigned by users
                pstmt.setString(2, new EditingUI().getAllTexts());
                // update
                pstmt.executeUpdate();
//                JOptionPane.showMessageDialog(null, "Successfully Saved");
            } catch (SQLException sqlE) {
                sqlE.printStackTrace();
            }

            System.exit(0);
        }
    }

    public String getResult() {
//        System.out.println(result);
        return titleField.getText();
    }

    public static void main(String[] args) {
        SaveTitle btf = new SaveTitle();
//        btf.setVisible(true);
    }


}
