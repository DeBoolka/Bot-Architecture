package dikanev.nikita.core.logic.commands.user;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.item.Ammo;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.JObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.AmmunitionController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserAmmunitionCommand extends Command {
    private static Gson gson = new Gson();

    public UserAmmunitionCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws ApiException, SQLException, NoSuchFieldException {
        return null;
    }

    @Override
    protected ApiObject ChoiceStartPointOfWork(User user, Parameter args, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        switch (commandParser.headers.getF(HEADER_ENTER_POINT).toLowerCase()) {
            case "get":
            case "add":
                return addAmmunition(args);
            case "delete":
            default:
                return super.ChoiceStartPointOfWork(user, args, commandParser);
        }
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[][]{
                new String[]{"userId", "name"},
        }, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "name", ((parameter, val) -> parameter.getF("name").length() > 127 || parameter.getF("name").isEmpty() ? "Incorrect name parameter." : null)
        ));
    }

    private ApiObject addAmmunition(Parameter params) throws SQLException {
        int userId;
        String ammoName;
        List<String> photo;
        try {
            userId = params.getIntF("userId");
            ammoName = params.getF("name");
            photo = params.getOrDefault("link", List.of());
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect parameter."));
        }

        Ammo ammo = AmmunitionController.addAmmunition(userId, ammoName, photo.toArray(new String[0]));
        return new JObject("Ammunition", gson.toJsonTree(ammo).getAsJsonObject());
    }

    private ApiObject getArrayApiObject(Ammo[] ammos) {
        JsonObject jsRoot = new JsonObject();
        JsonArray jsArray = new JsonArray();

        Arrays.stream(ammos).forEach(it -> jsArray.add(gson.toJson(it)));

        jsRoot.addProperty("type", "array");
        jsRoot.addProperty("typeObjects", "Ammunition");
        jsRoot.add("objects", jsArray);

        return new JObject(jsRoot);
    }
}
