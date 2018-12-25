package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.UserObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterUserCommand extends Command {

    private static Pattern pattern = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);

    public RegisterUserCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[][]{
                new String[]{"id_group", "s_name", "name", "email", "login", "password"},
                new String[]{"id_group", "s_name", "name", "email", "login"}
        },
                Map.of(
                        "s_name", (it, val) -> val.get(0).trim().isEmpty() || val.get(0).length() > 100 ? "Incorrect s_name parameter." : null,
                        "name", (it, val) -> val.get(0).trim().isEmpty() || val.get(0).length() > 100 ? "Incorrect name parameter." : null,
                        "email", (it, val) -> val.get(0).trim().isEmpty() || val.get(0).length() > 127 || !pattern.matcher(val.get(0)).matches()
                                ? "Incorrect email parameter." : null,
                        "login", (it, val) -> val.get(0).trim().isEmpty() || val.get(0).length() > 40 ? "Incorrect login parameter." : null
                )
        );
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        int idGroup;
        String sName;
        String name;
        String email;
        String login;
        String password;
        try {
            idGroup = params.getIntF("id_group");
            sName = params.getFOrDefault("s_name", "");
            name = params.getFOrDefault("name", "");
            email = params.getFOrDefault("email", "");
            login = params.getFOrDefault("login", "");
            password = params.getF("password");
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of param."));
        }

        if (sName.equals("")
                || sName.length() > 100
                || name.equals("")
                || name.length() > 100
                || email.equals("")
                || email.length() > 127
                || !pattern.matcher(email).matches()
                || login.isEmpty()
                || login.length() > 40
        ){
            return new ExceptionObject(new InvalidParametersException("Incorrect surname, name, login or email param."));
        } else if(idGroup <= 0) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id group param."));
        }

        int idUser;
        try {
            idUser = UserController.getInstance().registerUser(email, login, sName, name, idGroup, password);
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Create a user in the database."));
        }

        return new UserObject(idUser, idGroup, sName, name);
    }

}
