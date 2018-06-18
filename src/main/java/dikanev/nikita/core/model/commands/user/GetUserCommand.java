package dikanev.nikita.core.model.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.GroupObject;
import dikanev.nikita.core.api.objects.UserObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controller.groups.GroupController;
import dikanev.nikita.core.controller.users.UserController;
import dikanev.nikita.core.model.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class GetUserCommand extends Command {
    public GetUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        int id;
        try {
            id = getInt("id", args);
        } catch (InvalidParametersException e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        int idGroup;
        String sName;
        String name;
        try {
            Map<String, Object> data = UserController.getInstance().getData(id);

            if (data.size() < 3) {
                return new ExceptionObject(new NotFoundException("User not found"));
            }

            idGroup = (Integer) data.get("id_group");
            sName = (String) data.get("s_name");
            name = (String) data.get("name");
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("DB error"));
        }

        return new UserObject(id, idGroup, sName, name);
    }
    
}
