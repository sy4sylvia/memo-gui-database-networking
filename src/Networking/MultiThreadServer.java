package Networking;


import Database.Connect;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.*;

public class MultiThreadServer extends JFrame implements Runnable {
    // Text area for displaying contents
    private JTextArea ta;

    // Number a client
    private int clientNo = 0;
    private String allMemoNames;

    public MultiThreadServer() {
        ta = new JTextArea(10,10);
        JScrollPane sp = new JScrollPane(ta);
        this.add(sp);
        this.setTitle("MultiThreadServer");
        this.setSize(400,200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            ta.append("MultiThreadServer started at " + new Date() + '\n');

            while (true) {
                // Listen for a new connection request
                synchronized (serverSocket) {
                    Socket socket = serverSocket.accept();
                    // Increment clientNo
                    clientNo++;
                    ta.append('\n' + "Starting thread for client " + clientNo + " at " + new Date() + '\n');

                    new Thread(new HandleAClient(socket, clientNo)).start();
                }
            }
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }

    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        private int clientNum;

        /** Construct a thread */
        public HandleAClient(Socket socket, int clientNum) {
            this.socket = socket;
            this.clientNum = clientNum;
        }

        /** Run a thread */
        public void run() {
            try {
                // Create an input stream from the client
                ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
                // Create an output stream to the client
                ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());

                // Continuously serve the client
                while (true) {
                    // Read from input
                    Object object = inputFromClient.readObject();
                    System.out.println(object);

                    MemoData md = (MemoData) object; //cast

                    System.out.println("got object " + object.toString());

                    //save to database, and this is no longer implemented in EditingUI.java
                    String nameOfMemo = md.getName();
                    String contents = md.getContents();

                    if (nameOfMemo != null && contents != null) {
                        String sql = "INSERT INTO memos(name, contents) VALUES(?,?)";
                        Connection conn = Connect.connect();
                        try {
                            PreparedStatement pstmt = conn.prepareStatement(sql);
                            // set the corresponding param
                            pstmt.setString(1, nameOfMemo);
                            pstmt.setString(2, contents);
                            // update
                            pstmt.executeUpdate();

                        } catch (SQLException sqlE) {
                            sqlE.printStackTrace();
                        }

                        String sqlName = "SELECT name FROM memos WHERE name > ?";
                        conn = Connect.connect();
                        StringBuilder sb = new StringBuilder();
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlName)) {
                            pstmt.setString(1, "");
                            ResultSet rs = pstmt.executeQuery();

                            while (rs.next()) {
                                sb.append(rs.getString("name") + '\n');
//                            outputToClient.writeObject(sb.toString());
                                System.out.println(rs.getString("name") + "\t");
                            }
                            //output stream
                            //Send back the names of memos to the client
                            allMemoNames = sb.toString();
                            outputToClient.writeObject(allMemoNames);
                            outputToClient.flush(); //make sure all are flushed

                            ta.append("Area found: " + allMemoNames);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch(IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        MultiThreadServer mts = new MultiThreadServer();
        mts.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mts.setVisible(true);
    }
}