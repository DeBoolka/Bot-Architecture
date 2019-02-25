package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.item.parameter.Parameter;

import java.sql.SQLException;
import java.util.Map;

public class UpdateUserCommand extends Command {
    public UpdateUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User userIn, Parameter params) throws NoSuchFieldException, SQLException {
        int userId = params.getIntF("userId");
        int groupId = 0;
        String name = null;
        String s_name = null;

        if (!params.contains("groupId", "name", "s_name")) {
            return new ExceptionObject(new InvalidParametersException("Parameter not found."));
        }

        if (params.contains("groupId")) {
            if (!params.isIntF("groupId")) {
                return new ExceptionObject(new InvalidParametersException("Incorrect groupId parameter."));
            }

            groupId = params.getIntF("groupId");
        }
        if (params.contains("name")) {
            name = params.getF("name");
        }
        if (params.contains("s_name")) {
            s_name = params.getF("s_name");
        }

        User user = new User(userId, groupId, s_name, name);

        return UserController.updateBaseInfo(user) ? new MessageObject("Ok")
                : new ExceptionObject(new InvalidParametersException("User not found."));
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) throws InvalidParametersException {
        return new PreparedParameter(new String[][]{
                new String[]{"userId"}
        }, Map.of("userId", (it, val) -> val.get(0).trim().isEmpty() || !params.isIntF("userId") ? "Incorrect userId parameter." : null)
        );
    }
}
