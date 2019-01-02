package dikanev.nikita.core.logic.commands.user.photo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.JObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AddPhotoCommand extends Command {
    public AddPhotoCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws NoSuchFieldException, SQLException, ApiException {
        int userId = params.getIntF("userId");
        List<String> links = params.get("link");

        for (int i = links.size() - 1; i >= 0; i--) {
            if (links.get(i).isEmpty()) {
                links.remove(i);
            }
        }

        Map<Integer, String> photosId = UserController.insertPhoto(userId, links.toArray(new String[]{}));
        JsonObject jsRoot = new JsonObject();
        JsonArray jsArray = new JsonArray();

        photosId.forEach((id, link) -> {
            JsonObject jsPhoto = new JsonObject();
            jsPhoto.addProperty("id", id);
            jsPhoto.addProperty("link", link);
            jsArray.add(jsPhoto);
        });

        jsRoot.addProperty("type", "array");
        jsRoot.addProperty("typeObjects", "photo");
        jsRoot.add("objects", jsArray);

        return new JObject(jsRoot);
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) throws InvalidParametersException {
        return new PreparedParameter(new String[]{"userId", "link"}, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "link", ((parameter, val) -> parameter.get("link").isEmpty() ? "Incorrect link parameter." : null)
        ));
    }
}
