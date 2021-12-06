package Networking;

//used, similar to the lecture code: StudentServer

import Database.Connect;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MemoServer {
    private ObjectOutputStream outputToFile;
    private ObjectInputStream inputFromClient;

    public MemoServer() {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server started ");

            // Create an object output stream
            outputToFile = new ObjectOutputStream( new FileOutputStream("memo.dat", true));



            while (true) {
                // Listen for a new connection request
                Socket socket = serverSocket.accept();

                // Create an input stream from the socket
                inputFromClient =
                        new ObjectInputStream(socket.getInputStream());

                // Read from input
                Object object = inputFromClient.readObject();

                //use SQL to save to database, and this is no longer implemented in EditingUI.java
                MemoData md = (MemoData) object; //cast

                //

                //

                //




                System.out.println("got object " + object.toString());




                ///
                String nameOfMemo = md.getName();
                String contents = md.getContents();


                String sql = "INSERT INTO memos(name, contents) VALUES(?,?)";
                Connection conn = Connect.connect();
                try {
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    // set the corresponding param
                    pstmt.setString(1, nameOfMemo);
//                System.out.println("--------\nthis is after inserting into the database");
//                System.out.println(resultTitle); ///by this time, name has not be assigned by users
                    pstmt.setString(2, contents);
                    // update
                    pstmt.executeUpdate();

                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }


                // Write to the file
//                StudentAddress s = (StudentAddress)object; // cast to the student objects
//                System.out.println("got object " + object.toString());
//                outputToFile.writeObject(object);
//                outputToFile.flush();
//                System.out.println("A new student object is stored");
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } finally {
            try{
                inputFromClient.close();
                outputToFile.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        //now we have input from the editing ui, we should take the data and store it in the database
    }


    public static void main(String[] args) {
        new MemoServer();
    }
}
