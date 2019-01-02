package dikanev.nikita.core.logic.commands.user.photo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.JObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetPhotoCommand extends Command {
    public GetPhotoCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws SQLException {
        List<Integer> photosId;

        try {
            photosId = params.get("photoId").stream().map(Integer::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect photoId parameter."));
        }

        Map<Integer, String> photos = UserController.getPhoto(photosId.toArray(new Integer[]{}));

        JsonObject jsRoot = new JsonObject();
        JsonArray jsArray = new JsonArray();

        photos.forEach((id, link) -> {
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
        return new PreparedParameter(new String[]{"photoId"}, Map.of(
                "photoId", ((parameter, val) -> !parameter.get("photoId").isEmpty() ? null : "Incorrect photoId parameter.")
        ));

    }
}
