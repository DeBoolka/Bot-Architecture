package dikanev.nikita.core.model.commands.group;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.GroupObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controller.groups.GroupController;
import dikanev.nikita.core.model.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class GetGroupCommand extends Command {
    public GetGroupCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        int idGroup;
        try {
            idGroup = getInt("id", args);
        } catch (InvalidParametersException e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        String name;
        try {
            name = GroupController.getInstance().getName(idGroup);
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("DB error."));
        }

        return new GroupObject(idGroup, name);
    }
}
