package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectData {
    private static final String url = "jdbc:sqlite:/Users/siyaguo/Desktop/MemoDatabase/editedContents.db";

    public static void selectAll() {

        String sql = "SELECT * FROM memos";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            while (rs.next()) {

                String sth = rs.getString("name");
                String sthMore = rs.getString("contents");

                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("contents"));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void selectColumn() {

        List<String> columns = new ArrayList<>();
//        List<String> secondColumn = new ArrayList<>();

        String sql = "select * from memos LIMIT 0";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData mrs = rs.getMetaData();
            for(int i = 1; i <= mrs.getColumnCount(); i++) {
                columns.add(mrs.getColumnLabel(i));

            }

            for (int i = 0; i < columns.size(); i++) {
                System.out.print(columns.get(i) + " ");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void selectName(String name) {
        String sql = "SELECT name FROM memos WHERE name > ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("name") + "\t");
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
//        printRecordFromCourse();
//        app.selectAll();
//        app.getCapacityGreaterThan("");
//        app.getIdGreaterThan(6);
        selectName("");
//        selectColumn();
    }
}
