package dikanev.nikita.core.logic.commands.group;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.GroupController;
import dikanev.nikita.core.logic.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class DeleteGroupCommand extends Command {
    public DeleteGroupCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        int idGroup;
        try {
            idGroup = getInt("id", args);
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        boolean response;
        try {
            response = GroupController.getInstance().deleteGroup(idGroup);
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Create a group in the database."));
        }

        return new MessageObject(response ? "Ok" : "No");
    }
}
