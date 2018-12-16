package dikanev.nikita.core.logic.commands.group.access;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.objects.*;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GetAccessGroupCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(GetAccessGroupCommand.class);

    public GetAccessGroupCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        int idGroup;
        List<String> commands;
        try {
            idGroup = params.getIntF("id_group");
            commands = params.get("cmd");
            if (commands == null) {
                return new ExceptionObject(new InvalidParametersException("Parameter cmd not found"));
            }
        } catch (NoSuchFieldException e) {
            return new ExceptionObject(new InvalidParametersException("Parameter id_group not found"));
        }

        Map<String, Boolean> commandsAccess;
        try {
            commandsAccess = AccessGroupController.getInstance().getAccessGroup(idGroup, commands);
        } catch (SQLException e) {
            return new ExceptionObject(new UnidentifiedException(e.getMessage()));
        }

        ArrayObject arrayObject = new ArrayObject(new AccessGroupObject().getType());
        for (Map.Entry<String, Boolean> access : commandsAccess.entrySet()) {
            arrayObject.add(new AccessGroupObject(idGroup, access.getKey(), access.getValue()));
        }

        return arrayObject;
    }
}
