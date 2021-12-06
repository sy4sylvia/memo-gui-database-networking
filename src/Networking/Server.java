package Networking;

import Database.SelectData;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import javax.swing.*;

public class Server extends JFrame {
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();

    // The byte array for sending and receiving datagram packets
    private byte[] buf = new byte[65536];

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        // Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!
        this.setLocationRelativeTo(null);

        try {
            // Create a server socket
            DatagramSocket socket = new DatagramSocket(8000);
            jta.append("Server started at " + new Date() + '\n');

            // Create a packet for receiving data
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

            // Create a packet for sending data
            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

            while (true) {
                // Initialize buffer for each iteration
                Arrays.fill(buf, (byte)0);

                // Receive Memo from the client in a packet
                socket.receive(receivePacket);
                jta.append("The client host name is " +
                        receivePacket.getAddress().getHostName() +
                        " and port number is " + receivePacket.getPort() + '\n');
                jta.append("Memo received from client is " +
                        new String(buf).trim() +  '\n');

                // Compute area
                SelectData.selectAll();

                StringBuilder sb = new StringBuilder();
                String url = "jdbc:sqlite:/Users/siyaguo/Desktop/MemoDatabase/editedContents.db";
                String sql = "SELECT name FROM memos";

                try (Connection conn = DriverManager.getConnection(url);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    while (rs.next()) {
                        String sth = rs.getString("name");
                        String sthMore = rs.getString("contents");
                        sb.append(sth);
                        sb.append(sthMore);
                        jta.append("Memo received from client is " + "sb。。。。。。。。。。。。。。。。。。。" + '\n');
//                        System.out.println(rs.getString(rs.getInt("id") +  "\t" + rs.getString("name") + "\t" + "\t"));
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                // Send area to the client in a packet
                sendPacket.setAddress(receivePacket.getAddress());
                sendPacket.setPort(receivePacket.getPort());
                sendPacket.setData(sb.toString().getBytes());
                socket.send(sendPacket);
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
