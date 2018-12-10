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

public class RegisterUserCommand extends Command {

    public RegisterUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter args) {
        int idGroup;
        String sName;
        String name;
        String email;
        String password;
        try {
            idGroup = args.getIntF("id_group");
            sName = args.getFOrDefault("s_name", "");
            name = args.getFOrDefault("name", "");
            email = args.getFOrDefault("email", "");
            password = args.getF("password");
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of param."));
        }

        if (sName.equals("")
                || name.equals("")
                || email.equals("")
                || email.length() > 127
                || sName.length() > 100
                || name.length() > 100) {
            return new ExceptionObject(new InvalidParametersException("Incorrect surname, name or email param."));
        } else if(idGroup <= 0) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id group param."));
        }

        int idUser;
        try {
            idUser = UserController.getInstance().registerUser(email, sName, name, idGroup, password);
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Create a user in the database."));
        }

        return new UserObject(idUser, idGroup, sName, name);
    }

}
