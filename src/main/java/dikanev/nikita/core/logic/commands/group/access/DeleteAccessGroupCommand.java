package dikanev.nikita.core.logic.commands.group.access;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.commands.CommandController;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;

public class DeleteAccessGroupCommand extends Command {
    public DeleteAccessGroupCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        int idGroup;
        int idCommand = -1;
        String nameCommand = null;
        try {
            idGroup = params.getIntF("id_group");
            if (params.contains("id_command")) {
                idCommand = params.getIntF("id_command");
            } else if (params.contains("name")) {
                nameCommand = params.getF("name");
            } else {
                return new ExceptionObject(new InvalidParametersException("Parameters name or id_command not found"));
            }
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of param."));
        }

        boolean response;
        try {
            if (nameCommand != null) {
                idCommand = CommandController.getInstance().getId(nameCommand);
            }
            response = AccessGroupController.getInstance().deleteAccess(idGroup, idCommand);
        } catch (SQLException | NotFoundException e) {
            return new ExceptionObject(new InvalidParametersException("Delete a group in the database."));
        }

        return new MessageObject(response ? "Ok" : "No");
    }
}
