package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    public static Connection connect() {
        String url = "jdbc:sqlite:/Users/siyaguo/Desktop/MemoDatabase/editedContents.db";
        Connection c = null;
        try {
            c = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }
}