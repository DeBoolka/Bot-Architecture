package dikanev.nikita.core.controllers.db;

import dikanev.nikita.core.api.InviteIntoSystem;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

        res.close();
        return inv;
    }

    public static List<InviteIntoSystem> getInvitesIntoSystemOfUser(int userId) throws SQLException {
        String sql = "SELECT user_creator, id_group, invite, date_create " +
                "FROM invites " +
                "WHERE user_creator = ?";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, userId);

        ResultSet res = prStatement.executeQuery();

        List<InviteIntoSystem> invites = new ArrayList<>();

        while (res.next()) {
            int idCreator = res.getInt("user_creator");
            int groupId = res.getInt("id_group");
            String inv = res.getString("invite");
            Timestamp time = res.getTimestamp("date_create");

            invites.add(new InviteIntoSystem(idCreator, groupId, inv, time));
        }

        res.close();
        return invites;
    }

    public static int applyInviteIntoSystem(int userId, String invite) throws SQLException {
        String sql = "SELECT APPLY_INVITE_INTO_SYSTEM(?, ?) AS groupId";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, invite);
        prStatement.setInt(2, userId);

        ResultSet res = prStatement.executeQuery();

        int groupId = -1;
        while (res.next()) {
            groupId = res.getInt("groupId");
        }

        res.close();
        return groupId;
    }
}
