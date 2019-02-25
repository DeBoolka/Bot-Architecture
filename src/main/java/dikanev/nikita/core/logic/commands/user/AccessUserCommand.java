package dikanev.nikita.core.logic.commands.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.JObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.item.parameter.Parameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AccessUserCommand extends Command {
    public AccessUserCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws ApiException, SQLException, NoSuchFieldException {
        return new ExceptionObject(new NotFoundException("Command not found."));
    }

    @Override
    protected ApiObject ChoiceStartPointOfWork(User user, Parameter params, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        switch (commandParser.headers.getF(HEADER_ENTER_POINT).toLowerCase()) {
            case "access":
                return checkAccess(params);
            default:
                return super.ChoiceStartPointOfWork(user, params, commandParser);
        }
    }

    private ApiObject checkAccess(Parameter params) throws NoSuchFieldException {
        int userId = params.getIntF("userId");
        List<String> commands = params.get("cmd");
        int groupId;
        Map<String, Boolean> commandsAccess;

        try {
            groupId = UserController.getGroupId(userId);
            commandsAccess = AccessGroupController.getAccessGroup(groupId, commands);
        } catch (Exception e) {
            return new ExceptionObject(new UnidentifiedException(e.getMessage()));
        }

        JsonArray accesses = new JsonArray();
        commandsAccess.forEach((key, val) -> {
            JsonObject jsCmd = new JsonObject();
            jsCmd.addProperty("command", key);
            jsCmd.addProperty("access", val);
            accesses.add(jsCmd);
        });

        JsonObject jsResp = new JsonObject();
        jsResp.addProperty("userId", userId);
        jsResp.addProperty("groupId", groupId);
        jsResp.add("accesses", accesses);

        return new JObject("accesses", jsResp);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[][]{
                new String[]{"userId", "cmd"},
        }, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "cmd", ((parameter, val) -> parameter.get("cmd").size() > 0 ? null : "Incorrect cmd parameter.")
        ));
    }
}
