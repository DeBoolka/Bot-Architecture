package dikanev.nikita.core.logic.commands.user.invite;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.InviteController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.item.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class CreateInvitesIntoSystemCommand extends Command {

    private static final Logger LOG = LoggerFactory.getLogger(CreateInvitesIntoSystemCommand.class);

    public CreateInvitesIntoSystemCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws NoSuchFieldException, SQLException {
        String invite = InviteController.createIntoSystem(
                params.getIntF("userId"), params.getIntF("groupId")
        );

        if (invite == null) {
            LOG.warn("Error creating invite. Invite is null.");
            return new ExceptionObject(new UnidentifiedException("Error creating invite."));
        }

        return new MessageObject(invite);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) throws InvalidParametersException {
        return new PreparedParameter(new String[]{"userId", "groupId"}, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "groupId", ((parameter, val) -> parameter.isIntF("groupId") ? null : "Incorrect groupId parameter.")
        ));
    }
}
