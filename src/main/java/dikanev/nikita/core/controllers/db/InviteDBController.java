package dikanev.nikita.core.controllers.db;

import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InviteDBController {

    private static final Logger LOG = LoggerFactory.getLogger(InviteDBController.class);

    public static String createIntoSystem(int userId, int groupId) throws SQLException {
        String sql = "SELECT CREATE_INVITE_INTO_SYSTEM(?, ?) AS inv";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, userId);
        prStatement.setInt(2, groupId);

        String inv = null;
        ResultSet res = prStatement.executeQuery();

        while (res.next()) {
            inv = res.getString("inv");
        }

        return inv;
    }
}
