package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        // database and table creation
        String url = "jdbc:sqlite:" + args[1];
        //String url = "jdbc:sqlite:c:\\sqlite\\card.s3db";
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {

            try (Statement statement = con.createStatement()) {

                /*statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "number TEXT, " +
                        "pin INTEGER, " +
                        "balance INTEGER) ");*/
                statement.execute("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER, " +
                        "number TEXT, " +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0);");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Menu menu = new Menu(dataSource);
    }
}