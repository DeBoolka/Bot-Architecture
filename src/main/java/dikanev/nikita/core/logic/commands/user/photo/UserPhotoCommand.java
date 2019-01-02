package dikanev.nikita.core.logic.commands.user.photo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.JObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPhotoCommand extends Command {
    public UserPhotoCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws ApiException, SQLException, NoSuchFieldException {
        return null;
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) throws InvalidParametersException {
        return new PreparedParameter(new String[][]{
                new String[]{"photoId"},
                new String[]{"userId", "link"}
        }, Map.of(
                "photoId", ((parameter, val) -> !parameter.get("photoId").isEmpty() ? null : "Incorrect photoId parameter."),
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "link", ((parameter, val) -> parameter.get("link").isEmpty() ? "Incorrect link parameter." : null)
        ));
    }

    @Override
    protected ApiObject ChoiceStartPointOfWork(User user, Parameter args, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        switch (commandParser.headers.getF(HEADER_ENTER_POINT).toLowerCase()) {
            case "get":
                return getPhoto(args);
            case "add":
                return addPhoto(args);
            case "delete":
                return deletePhoto(args);
            default:
                return super.ChoiceStartPointOfWork(user, args, commandParser);
        }
    }

    private ApiObject deletePhoto(Parameter params) throws SQLException {
        List<Integer> photosId = new ArrayList<>();

        try {
            params.get("photoId").forEach(it -> photosId.add(Integer.valueOf(it)));
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect photoId parameter."));
        }

        boolean isDelete = UserController.deletePhoto(photosId.toArray(new Integer[]{}));

        return new MessageObject(isDelete ? "Ok" : "No");
    }

    private ApiObject addPhoto(Parameter params) throws NoSuchFieldException, InvalidParametersException, SQLException {
        int userId = params.getIntF("userId");
        List<String> links = params.get("link");

        for (int i = links.size() - 1; i >= 0; i--) {
            if (links.get(i).isEmpty()) {
                links.remove(i);
            }
        }

        Map<Integer, String> photosId = UserController.insertPhoto(userId, links.toArray(new String[]{}));
        return getApiObject(photosId);
    }

    private ApiObject getPhoto(Parameter params) throws SQLException {
        List<Integer> photosId;

        try {
            photosId = params.get("photoId").stream().map(Integer::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect photoId parameter."));
        }

        Map<Integer, String> photos = UserController.getPhoto(photosId.toArray(new Integer[]{}));
        return getApiObject(photos);
    }

    private ApiObject getApiObject(Map<Integer, String> photos) {
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
}
