package dikanev.nikita.core.logic.commands.user.info;

import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.JObject;
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
        boolean hasParameterCol = params.contains("col");
        UserInfo userInfo = null;
        JsonObject js = null;

        if (params.contains("id")) {
            int userId = params.getIntF("id");

            if (hasParameterCol) {
                js = UserController.getUserAndUserInfo(userId, params.get("col").toArray(new String[]{}));
            } else {
                userInfo = UserController.getInfo(userId);
            }
        } else if (params.contains("login")) {
            String login = params.getF("login");

            if (hasParameterCol) {
                js = UserController.getUserAndUserInfoByLogin(login, params.get("col").toArray(new String[]{}));
            } else {
                userInfo = UserController.getInfoByLogin(login);
            }
        } else if (params.contains("email")) {
            String email = params.getF("email");

            if (hasParameterCol) {
                js = UserController.getUserAndUserInfoByEmail(email, params.get("col").toArray(new String[]{}));
            } else {
                userInfo = UserController.getInfoByEmail(email);
            }
        }

        if (!hasParameterCol && userInfo == null || hasParameterCol && js == null) {
            return new ExceptionObject(new InvalidParametersException("User information not found."));
        }

        if (!hasParameterCol) {
            return new UserInfoObject(
                    userInfo.getUserId(),
                    userInfo.getLogin(),
                    userInfo.getEmail(),
                    userInfo.getAge(),
                    userInfo.getPhone(),
                    userInfo.getCity(),
                    userInfo.getNameOnGame()
            );
        } else {
            return new JObject(js);
        }
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
