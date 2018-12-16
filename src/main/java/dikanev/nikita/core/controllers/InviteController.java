package dikanev.nikita.core.controllers;

import dikanev.nikita.core.controllers.db.InviteDBController;

import java.sql.SQLException;

public class InviteController {
    public static String createIntoSystem(int userId, int groupId) throws SQLException {
        return InviteDBController.createIntoSystem(userId, groupId);
    }

}
