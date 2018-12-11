package dikanev.nikita.core.logic.commands.group.access;

import com.google.common.primitives.Ints;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class CreateAccessGroupCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(CreateAccessGroupCommand.class);

    public CreateAccessGroupCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject work(User user, Parameter args) {
        if (!args.contains("id_group")) {
            return new ExceptionObject(new NotFoundException("Not found group id"));
        } else if (!args.contains("access")) {
            return new ExceptionObject(new NotFoundException("Not found access"));
        } else if (!args.contains("name") && !args.contains("id_command")) {
            return new ExceptionObject(new NotFoundException("Not found name or id_command"));
        }

        int idGroup;
        boolean access;
        try {
            idGroup = args.getIntF("id_group");
            access = Boolean.valueOf(args.getF("access"));
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("id_group or access is invalid."));
        }

        List<String> names;
        List<Integer> idCommands;
        boolean response;
        names = args.get("name");
        try {
            if (names == null) {
                try {
                    idCommands = args.getInt("id_command");
                } catch (NumberFormatException e) {
                    return new ExceptionObject(new InvalidParametersException("id_command is invalid."));
                }

                response = AccessGroupController.getInstance().createAccess(idGroup, Ints.toArray( idCommands), access);
            } else {
                response = AccessGroupController.getInstance().createAccess(idGroup, names.toArray(new String[0]), access);
            }
        } catch (SQLException e) {
            LOG.error("SQL request error: ", e);
            return new ExceptionObject(new UnidentifiedException("Server error."));
        }

        return new MessageObject(response ? "Ok" : "No");
    }
}
