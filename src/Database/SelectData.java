package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectData {
    private static final String url = "jdbc:sqlite:/Users/siyaguo/Desktop/MemoDatabase/editedContents.db";

    public static void selectAll() {

        String sql = "SELECT name FROM memos";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String sth = rs.getString("name");
                String sthMore = rs.getString("contents");
                System.out.println(rs.getString(rs.getInt("id") +  "\t" + rs.getString("name") + "\t" + "\t"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void printRecordFromCourse() throws SQLException {
        Connection dbConnection = null;
        Statement stmt = null;
        String printSQL = "SELECT * FROM memos";

        try {
            dbConnection = DriverManager.getConnection(url);
            stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(printSQL);

            List<Row> rows = new ArrayList<Row>();
            while (rs.next()) {
                Row row = new Row();
                row.setId(rs.getInt("id"));
                row.setName(rs.getString("name"));
                row.setContents(rs.getString("contents"));
                rows.add(row);
            }

// To display it:
//            for (Row row : rows) System.out.println(row);
            for (Row r : rows) System.out.println(r.getContents());
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                dbConnection.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }



    public static void main(String[] args) throws SQLException {
        SelectData app = new SelectData();
        printRecordFromCourse();
//        app.selectAll();
//        app.getCapacityGreaterThan("");
//        app.getIdGreaterThan(6);
    }
}
