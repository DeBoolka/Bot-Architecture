package dikanev.nikita.core.logic.commands.group.access;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.commands.CommandController;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.logic.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class DeleteAccessGroupCommand extends Command {
    public DeleteAccessGroupCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        int idGroup;
        int idCommand = -1;
        String nameCommand = null;
        try {
            idGroup = getInt("id_group", args);
            if (hasParameter("id_command", args)) {
                idCommand = getInt("id_command", args);
            } else {
                nameCommand = getString("name", args);
            }
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        boolean response;
        try {
            if (nameCommand != null) {
                idCommand = CommandController.getInstance().getId(nameCommand);
            }
            response = AccessGroupController.getInstance().deleteAccess(idGroup, idCommand);
        } catch (SQLException | NotFoundException e) {
            return new ExceptionObject(new InvalidParametersException("Delete a group in the database."));
        }

        return new MessageObject(response ? "Ok" : "No");
    }
}
