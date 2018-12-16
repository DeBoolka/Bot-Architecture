package dikanev.nikita.core.logic.commands.user.invite;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.groups.Group;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.GroupObject;
import dikanev.nikita.core.api.objects.UnknownObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.InviteController;
import dikanev.nikita.core.controllers.groups.GroupController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.Map;

public class ApplyInviteIntoSystem extends Command {

    public ApplyInviteIntoSystem(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws NoSuchFieldException, SQLException {
        int userId = params.getIntF("userId");
        String invite = params.getF("invite");

        int newGroup = InviteController.applyInviteIntoSystem(userId, invite);
        if (newGroup == -1) {
            return new ExceptionObject(new InvalidParametersException("Invite not found"));
        } else if (newGroup == -2) {
            return new ExceptionObject(new InvalidParametersException("User not found"));
        }

        return new GroupObject(newGroup, GroupController.getInstance().getName(newGroup));
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) throws InvalidParametersException {
        return new PreparedParameter(new String[]{"userId", "invite"}, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "invite", ((parameter, val) -> parameter.getF("invite").length() <= 10 ? null : "Incorrect invite.")
        ));

    }
}
