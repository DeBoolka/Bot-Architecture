package dikanev.nikita.core.model.commands.group.access;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controller.groups.AccessGroupController;
import dikanev.nikita.core.model.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class CreateAccessGroupCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(CreateAccessGroupCommand.class);

    public CreateAccessGroupCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Map<String, String[]> args) {
        int idGroup;
        int idCommand = -1;
        Integer[] idCommands = null;
        String nameCommand = null;
        String[] idCommandsArr = args.get("id_command");
        boolean access;
        try {
            idGroup = getInt("id_group", args);
            if (hasParameter("id_command", args)) {
                if (idCommandsArr.length > 1) {
                    idCommands = new Integer[idCommandsArr.length];
                    for (int i = 0; i < idCommandsArr.length; i++) {
                        idCommands[i] = Integer.valueOf(idCommandsArr[i]);
                    }
                } else {
                    idCommand = getInt("id_command", args);
                }
            } else {
                nameCommand = getString("name", args);
            }
            access = Boolean.valueOf(getString("access", args));
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of parameters."));
        }

        boolean response;
        try {
            if (idCommands != null) {
                response = AccessGroupController.getInstance().createAccess(idGroup, idCommands, access);
            } else {
                if (nameCommand != null) {
                    response = AccessGroupController.getInstance().createAccess(idGroup, nameCommand, access);
                } else if (idCommand >= 0) {
                    response = AccessGroupController.getInstance().createAccess(idGroup, idCommand, access);
                } else {
                    throw new InvalidParametersException("Parameter not found");
                }
            }
        } catch (SQLException | InvalidParametersException e) {
            LOG.debug("Create a group in the database: ", e);
            return new ExceptionObject(new InvalidParametersException("Create a group in the database."));
        }

        return new MessageObject(response ? "Ok" : "No");
    }
}
