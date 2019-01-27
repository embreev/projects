package server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService extends ConnectDB {

    public static void addUser(String login, String pass, String nickName) {
        String sql = String.format("INSERT INTO users (login, password, nickname) \n" +
                "VALUES ('%s', '%s', '%s')", login, pass, nickName);
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {
        String sql = String.format("SELECT nickname FROM users\n" +
                "WHERE login = '%s'\n" +
                "AND password = '%s'", login, pass);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
