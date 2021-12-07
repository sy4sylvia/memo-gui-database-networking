package Networking;

//not thread safe, use MultiThreadServer instead
import Database.Connect;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Date;

public class MemoServer {

    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;
    // Number a client
    private int clientNo = 0;

    public MemoServer() {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server started ");
            while (true) {
                // Listen for a new connection request
                Socket socket = serverSocket.accept();

                // Create an input stream from the socket
                inputFromClient = new ObjectInputStream(socket.getInputStream());
                // Create an output stream from the socket
                outputToClient = new ObjectOutputStream(socket.getOutputStream());

                // Read from input
                Object object = inputFromClient.readObject();
                System.out.println(object);

                MemoData md = (MemoData) object; //cast

                System.out.println("got object " + object.toString());

                //save to database, and this is no longer implemented in EditingUI.java
                String nameOfMemo = md.getName();
                String contents = md.getContents();

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

                //output stream
                // Send area back to the client
                String sqlName = "SELECT name FROM memos WHERE name > ?";
                conn = Connect.connect();
                StringBuilder sb = new StringBuilder();
                try (PreparedStatement pstmt = conn.prepareStatement(sqlName)) {
                    pstmt.setString(1, "");
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        sb.append(rs.getString("name")).append("\t");
                        System.out.println(rs.getString("name") + "\t");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                outputToClient.writeObject(sb.toString());
                System.out.println("A new student object is stored");
                outputToClient.flush(); //make sure all are flushed

            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {
            try{
                inputFromClient.close();
                outputToClient.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
//        MemoServer ms = new MemoServer();
        new MemoServer();
    }
}
