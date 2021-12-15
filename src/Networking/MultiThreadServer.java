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

    private int storedId;
    private String storedName;
    private String storedContents;

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
            ta.append("MultiThreadServer started at " + '\n' + new Date() + '\n');
            while (true) {
                // Listen for a new connection request
                synchronized (serverSocket) {
                    Socket socket = serverSocket.accept();
                    clientNo++;
                    ta.append('\n' + "Starting thread for client " + clientNo + " at " + new Date() + '\n');
                    new Thread(new HandleAClient(socket, clientNo)).start();
                }
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        private int clientNum;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        // Construct a thread
        public HandleAClient(Socket socket, int clientNum) {
            this.socket = socket;
            this.clientNum = clientNum;
        }

        //another constructor
        public HandleAClient(Socket socket, ObjectInputStream ois, ObjectOutputStream oos){
            this.socket = socket;
            this.ois = ois;
            this.oos = oos;
        }

        /** Run a thread */
        @Override
        public void run() {

            // Create an input stream from the client
            try {
                ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
                // Create an output stream to the client
                ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());

                // Read from input
                Object object = inputFromClient.readObject();
                MemoData md = (MemoData) object;
                String nameOfMemo = md.getName();
                String contents = md.getContents();

                // if a new name, then insert into the database;
                //if not a new name, warn the user of creating another one????
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
                    outputToClient.writeObject(object);
                    outputToClient.flush();
                }

                //now we've got the output stream, need to handle different requests for them
                if (nameOfMemo == null && contents == null) {
                    String sqlName = "SELECT name FROM memos WHERE name > ?";
                    Connection conn = Connect.connect();
                    StringBuilder sb = new StringBuilder();
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlName)) {
                        pstmt.setString(1, "");
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            sb.append(rs.getString("name") + '\n');
                            System.out.println(rs.getString("name") + "\t");
                        }

                        //output stream
                        //Send back the names of memos to the client
                        allMemoNames = sb.toString();
                        outputToClient.writeObject(allMemoNames);
                        outputToClient.flush(); //make sure all are flushed
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (nameOfMemo != null && contents == null) {
                    String sqlRestore = "SELECT id, name, contents FROM memos WHERE name = ?";
                    Connection conn = Connect.connect();
                    try {
                        PreparedStatement pstmt = conn.prepareStatement(sqlRestore);
                        pstmt.setString(1, nameOfMemo);
                        //here the string is the name
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            storedName = rs.getString("name");
                            storedContents = rs.getString("contents");
                        }
                        outputToClient.writeObject(storedName + '\n' + storedContents);
                        outputToClient.flush(); //make sure all are flushed
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
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