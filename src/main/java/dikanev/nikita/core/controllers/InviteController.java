package dikanev.nikita.core.controllers;

import dikanev.nikita.core.api.InviteIntoSystem;
import dikanev.nikita.core.controllers.db.InviteDBController;

import java.sql.SQLException;
import java.util.List;

public class InviteController {
    public static String createIntoSystem(int userId, int groupId) throws SQLException {
        return InviteDBController.createIntoSystem(userId, groupId);
    }

    public static List<InviteIntoSystem> getInvitesIntoSystemOfUser(int userId) throws SQLException {
        return InviteDBController.getInvitesIntoSystemOfUser(userId);
    }

    public static int applyInviteIntoSystem(int userId, String invite) throws SQLException {
        return InviteDBController.applyInviteIntoSystem(userId, invite);
    }

}
