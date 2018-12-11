package dikanev.nikita.core.logic.commands.group;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.GroupObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.GroupController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;

public class CreateGroupCommand extends Command {
    public CreateGroupCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject work(User user, Parameter args) {
        String name = null;
        boolean isIdParameter = args.contains("id");
        int id = -1;
        try {
            name = args.getF("name");
            if (isIdParameter) {
                id = args.getIntF("id");
            }
        } catch (NoSuchFieldException e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id param."));
        }

        if (name.equals("") || name.length() > 30 || isIdParameter && id <= 0) {
            return new ExceptionObject(new InvalidParametersException("Incorrect name param."));
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
