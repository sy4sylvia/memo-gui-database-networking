package Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

//create a database
public class CreateDB {
    public static void createNewDatabase(String fileName) {
        String url = "jdbc:sqlite:/Users/siyaguo/Desktop/MemoDatabase/" + fileName;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createNewDatabase("editedContents.db");
    }
}