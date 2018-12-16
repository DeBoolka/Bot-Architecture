package dikanev.nikita.core.logic.commands.user.create;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;

public class CreateTokenCommand extends Command {

    public CreateTokenCommand(int id) {
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
            id =  params.getIntF("id");
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect id param."));
        }

        try {
            String token = UserController.getInstance().createToken(id);
            return new MessageObject(token);
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("Create a user in the database."));
        }

    }
}
