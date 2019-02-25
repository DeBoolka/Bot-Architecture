package dikanev.nikita.core.logic.commands.group;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.GroupObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.GroupController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.item.parameter.Parameter;

import java.sql.SQLException;

public class GetGroupCommand extends Command {
    public GetGroupCommand(int id) {
        super(id);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        int idGroup;
        try {
            idGroup = params.getIntF("id");
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Insufficient number of param."));
        }

        String name;
        try {
            name = GroupController.getName(idGroup);
            if (name == null) {
                return new ExceptionObject(new NotFoundException("Group not found"));
            }
        } catch (SQLException e) {
            return new ExceptionObject(new InvalidParametersException("DB error"));
        }

        return new GroupObject(idGroup, name);
    }
}
