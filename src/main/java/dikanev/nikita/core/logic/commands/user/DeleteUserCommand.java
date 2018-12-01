package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;

import java.sql.SQLException;
import java.util.Map;

public class DeleteUserCommand extends Command {

    public DeleteUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        String[] id = args.get("id");
        if (id == null) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        } else if (id[0].equals("")) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id parameters."));
        }

        int idInt;
        try {
            idInt = Integer.parseInt(id[0]);
        } catch (NumberFormatException e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id user parameters."));
        }


        try {
            boolean hasDelete = UserController.getInstance().deleteUser(idInt);
            if (hasDelete) {
                return new MessageObject("Ok");
            } else {
                return new ExceptionObject(new InvalidParametersException("User is not found."));
            }
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Delete a user in the database."));
        }
    }
}
