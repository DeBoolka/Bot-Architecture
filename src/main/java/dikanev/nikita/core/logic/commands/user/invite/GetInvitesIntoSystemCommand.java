package dikanev.nikita.core.logic.commands.user.invite;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dikanev.nikita.core.api.InviteIntoSystem;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ArrayObject;
import dikanev.nikita.core.api.objects.UnknownObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.InviteController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetInvitesIntoSystemCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(GetInvitesIntoSystemCommand.class);

    public GetInvitesIntoSystemCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws NoSuchFieldException, SQLException {
        int userId = params.getIntF("userId");
        List<InviteIntoSystem> invites = InviteController.getInvitesIntoSystemOfUser(userId);
        JsonElement element = getElement(invites);

        return new UnknownObject("array", element);
    }

    private static JsonElement getElement(List<InviteIntoSystem> invites) {
        JsonArray arr = new JsonArray();
        invites.forEach(it ->{
            JsonObject obj = new JsonObject();
            obj.add("userId", new JsonPrimitive(it.getUserCreator()));
            obj.add("groupId", new JsonPrimitive(it.getGroupId()));
            obj.add("invite", new JsonPrimitive(it.getInvite()));
            obj.add("time", new JsonPrimitive(it.getTime().toString()));

            arr.add(obj);
        });

        return arr;
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) throws InvalidParametersException {
        return new PreparedParameter(new String[]{"userId"}, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter.")
        ));
    }
}
