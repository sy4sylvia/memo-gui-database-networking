package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {
    public static void createNewTable() {
        String url = "jdbc:sqlite:/Users/siyaguo/Desktop/MemoDatabase/editedContents.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS memos (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	contents text NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createNewTable();
        System.out.println("A new table has been created.");
    }
}
