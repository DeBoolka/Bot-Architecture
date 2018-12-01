package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.UserObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class NewUserCommand extends Command {

    public NewUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        int idGroup;
        String sName;
        String name;
        try {
            idGroup = getInt("id_group", args);
            sName = getString("s_name", args);
            name = getString("name", args);
        } catch (InvalidParametersException e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        if (sName.equals("") || name.equals("") || sName.length() > 30 || name.length() > 30) {
            return new ExceptionObject(new InvalidParametersException("Incorrect surname or name parameters."));
        } else if(idGroup <= 0) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id group parameters."));
        }

        int idUser;
        try {
            try {
                idUser = getInt("id", args);
                idUser = UserController.getInstance().createUser(idUser, sName, name, idGroup);
            } catch (InvalidParametersException e) {
                if (args.get("id") == null) {
                    idUser = UserController.getInstance().createUser(sName, name, idGroup);
                } else {
                    return new ExceptionObject(new InvalidParametersException("Incorrect id parameters."));
                }
            }
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Create a user in the database."));
        }

        return new UserObject(idUser, idGroup, sName, name);
    }

}
