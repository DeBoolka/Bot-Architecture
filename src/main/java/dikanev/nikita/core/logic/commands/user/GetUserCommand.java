package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.UserObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.Map;

public class GetUserCommand extends Command {
    public GetUserCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        int id;
        try {
            id = params.getIntF("id");
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of param."));
        }

        int idGroup;
        String sName;
        String name;
        try {
            Map<String, Object> data = UserController.getData(id);

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
