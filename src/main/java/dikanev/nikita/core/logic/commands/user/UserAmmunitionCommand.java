package dikanev.nikita.core.logic.commands.user;

import com.google.gson.Gson;
import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.item.Ammo;
import dikanev.nikita.core.api.objects.*;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.AmmunitionController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.ArrayList;
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
    protected ApiObject ChoiceStartPointOfWork(User user, Parameter params, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        switch (commandParser.headers.getF(HEADER_ENTER_POINT).toLowerCase()) {
            case "get":
                return getAmmunition(params);
            case "add":
                return addAmmunition(params);
            case "delete":
                return deleteAmmunition(params);
            default:
                return super.ChoiceStartPointOfWork(user, params, commandParser);
        }
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[][]{
                new String[]{"userId", "name"},
                new String[]{"userId"},
                new String[]{"ammunitionId"},
        }, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "ammunitionId", ((parameter, val) -> parameter.isInt("ammunitionId") ? null : "Incorrect ammunitionId parameter."),
                "name", ((parameter, val) -> parameter.getF("name").length() > 127 || parameter.getF("name").isEmpty() ? "Incorrect name parameter." : null)
        ));
    }

    private ApiObject getAmmunition(Parameter params) throws SQLException {
        int userId;
        int indent;
        int count;
        try {
            userId = params.getIntF("userId");
            indent = params.getIntFOrDefault("indent", 0);
            count = params.getIntFOrDefault("count", 5);

            if (indent < 0 || count <= 0) {
                return new ExceptionObject(new InvalidParametersException("Incorrect count or indent parameter."));
            }
        } catch (NoSuchFieldException | NumberFormatException ex) {
            return new ExceptionObject(new InvalidParametersException("Incorrect count or indent parameter."));
        }

        List<Ammo> ammunition = AmmunitionController.getAmmunition(userId, indent, count);
        if (ammunition == null) {
            ammunition = new ArrayList<>();
        }

        return new SimpleArrayObject(gson.toJsonTree(ammunition).getAsJsonArray());
    }

    private ApiObject addAmmunition(Parameter params) throws SQLException {
        if (!params.contains("name")) {
            return new ExceptionObject(new InvalidParametersException("Ammunition name is not found."));
        }

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

    private ApiObject deleteAmmunition(Parameter params) throws SQLException {
        List<Integer> ammunitionId = params.getInt("ammunitionId");

        boolean isGoodDelete = AmmunitionController.deleteAmmunition(ammunitionId.toArray(new Integer[0]));
        return new MessageObject(isGoodDelete ? "Ok" : "No");
    }

}
