package server;

import java.sql.*;

public class BlackListService {
    private static Connection connection;
    private static Statement stmt;

    static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String getMyID(String nickName) {
        String sql = String.format("SELECT id FROM users WHERE nickname = '%s'", nickName);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String checkUserOnBlackList(ClientHandler ch, String nickName) {
        String id = getMyID(ch.getNickName());
        String sql = String.format("SELECT id FROM blacklist \n" +
                "WHERE id_user = '%s' \n" +
                "AND nickname = '%s'", id, nickName);
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void addUserOnBlackList(ClientHandler ch, String nickName) {
        String id = getMyID(ch.getNickName());
        if (id != null) {
            String sql = String.format("INSERT INTO blacklist (id_user, nickname) VALUES ('%s', '%s')", id, nickName);
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void removeUserFromBlackList(ClientHandler ch, String nickName) {
        String id = getMyID(ch.getNickName());
        String sql = String.format("DELETE FROM blacklist WHERE id_user = '%s' AND nickname = '%s'", id, nickName);
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//        connect();
//        System.out.println(getMyID("nick1"));
////        addUserOnBlackList(getMyID("nick1"), "nick2");
//        System.out.println(checkUserOnBlackList(getMyID("nick1"), "nick3"));
//        disconnect();
//    }
}
