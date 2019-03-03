package dikanev.nikita.core.logic.commands.user.info;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.api.users.UserInfo;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.item.parameter.Parameter;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

public class UpdateUserInfoCommand extends Command {
    public UpdateUserInfoCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[]{"id"}, Map.of(
                "id", ((parameter, val) -> parameter.isIntF("id") ? null : "Incorrect id parameter.")
        ));
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws NoSuchFieldException, SQLException {
        int userId = params.getIntF("id");
        String login = null;
        String email = null;
        Date age = null;
        String phone = null;
        String city = null;
        String gameOnName = null;

        if (!params.contains("login", "email", "age", "phone", "city", "gameOnName")) {
            return new ExceptionObject(new InvalidParametersException("Parameter not found."));
        }

        if (params.contains("login")) {
            login = params.getF("login");
        }
        if (params.contains("email")) {
            email = params.getF("email");
        }
        if (params.contains("age")) {
            age = Date.valueOf(params.getF("age"));
        }
        if (params.contains("phone")) {
            phone = params.getF("phone");
        }
        if (params.contains("city")) {
            city = params.getF("city");
        }
        if (params.contains("gameOnName")) {
            gameOnName = params.getF("gameOnName");
        }

        UserInfo userInfo = new UserInfo(userId, login, email, age, phone, city, gameOnName);

        boolean isUpdate = UserController.updateInfo(userInfo);
        if (isUpdate) {
            return new MessageObject("OK");
        } else {
            return new ExceptionObject(new InvalidParametersException("User not found."));
        }
    }
}
