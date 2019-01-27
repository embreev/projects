package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectDB {
    protected static Connection connection;
    protected static Statement stmt;

    static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
