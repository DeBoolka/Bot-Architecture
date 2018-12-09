package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.URLParameter.Parameter;

import java.sql.SQLException;
import java.util.Map;

public class DeleteUserCommand extends Command {

    public DeleteUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter args) {
        int id;
        try {
            id = args.getIntF("id");
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id parameter."));
        }


        try {
            boolean hasDelete = UserController.getInstance().deleteUser(id);
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
