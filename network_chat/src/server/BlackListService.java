package server;

import java.sql.*;

public class BlackListService extends ConnectDB{

    static String checkUserOnBlackList(ClientHandler ch, String nickName) {
        String sql = String.format("SELECT * FROM blacklist \n" +
                "WHERE nickname = '%s' \n" +
                "AND block_nickname = '%s'", ch.getNickName(), nickName);
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
        if (checkUserOnBlackList(ch, nickName) == null) {
            String sql = String.format("INSERT INTO blacklist (nickname, block_nickname) \n" +
                    "VALUES ('%s', '%s')", ch.getNickName(), nickName);
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void removeUserFromBlackList(ClientHandler ch, String nickName) {
        String sql = String.format("DELETE FROM blacklist WHERE nickname = '%s' \n" +
                "AND block_nickname = '%s'", ch.getNickName(), nickName);
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
