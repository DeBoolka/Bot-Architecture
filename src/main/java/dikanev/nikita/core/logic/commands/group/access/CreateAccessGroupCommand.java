package dikanev.nikita.core.logic.commands.group.access;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.logic.commands.Command;
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
        String[] idCommandsArr = args.get("id_command");
        String[] nameCommands = args.get("name");
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
                if (nameCommands != null) {
                    response = AccessGroupController.getInstance().createAccess(idGroup, nameCommands, access);
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
