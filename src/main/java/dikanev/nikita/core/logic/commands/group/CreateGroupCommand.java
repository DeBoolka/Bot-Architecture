package dikanev.nikita.core.logic.commands.group;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.GroupObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.GroupController;
import dikanev.nikita.core.logic.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class CreateGroupCommand extends Command {
    public CreateGroupCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        String name = null;
        boolean isIdParameter = hasParameter("id", args);
        int id = -1;
        try {
            name = getString("name", args);
            if (isIdParameter) {
                id = getInt("id", args);
            }
        } catch (InvalidParametersException e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        if (name.equals("") || name.length() > 30 || isIdParameter && id <= 0) {
            return new ExceptionObject(new InvalidParametersException("Incorrect name parameters."));
        }

        try {
            if (isIdParameter) {
                id = GroupController.getInstance().createGroup(name, id);
            } else {
                id = GroupController.getInstance().createGroup(name);
            }
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Create a group in the database."));
        }

        return new GroupObject(id, name);
    }
}
