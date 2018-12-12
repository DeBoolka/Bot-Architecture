package dikanev.nikita.core.logic.commands.user.info;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.UserInfoObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.api.users.UserInfo;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.Map;

public class GetUserInfoCommand extends Command {
    public GetUserInfoCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws NoSuchFieldException, SQLException {
        UserInfo userInfo = null;
        if (params.contains("id")) {
            int userId = params.getIntF("id");
            userInfo = UserController.getInfo(userId);
        } else if (params.contains("login")) {
            String login = params.getF("login");
            userInfo = UserController.getInfoByLogin(login);
        } else if (params.contains("email")) {
            String email = params.getF("email");
            userInfo = UserController.getInfoByEmail(email);
        }

        if (userInfo == null) {
            return new ExceptionObject(new InvalidParametersException("User information not found."));
        }

        return new UserInfoObject(
                userInfo.getUserId(),
                userInfo.getLogin(),
                userInfo.getEmail(),
                userInfo.getAge(),
                userInfo.getPhone(),
                userInfo.getCity(),
                userInfo.getNameOnGame()
        );
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[][]{
                new String[]{"id"},
                new String[]{"login"},
                new String[]{"email"}
        }, Map.of(
                "id", (parameter, val) -> parameter.isIntF("id") ? null : "Incorrect id parameter.",
                "login", (parameter, val) -> !parameter.getF("login").isEmpty() ? null : "Incorrect login parameter.",
                "email", (parameter, val) -> !parameter.getF("email").isEmpty() ? null : "Incorrect email parameter."
        ));
    }
}
