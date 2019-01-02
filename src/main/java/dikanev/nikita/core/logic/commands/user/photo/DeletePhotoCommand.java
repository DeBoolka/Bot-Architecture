package dikanev.nikita.core.logic.commands.user.photo;

import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.users.UserController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.parameter.Parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeletePhotoCommand extends Command {
    public DeletePhotoCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) throws SQLException {
        List<Integer> photosId = new ArrayList<>();

        try {
            params.get("photoId").forEach(it -> photosId.add(Integer.valueOf(it)));
        } catch (Exception e) {
            return new ExceptionObject(new InvalidParametersException("Incorrect photoId parameter."));
        }

        boolean isDelete = UserController.deletePhoto(photosId.toArray(new Integer[]{}));

        return new MessageObject(isDelete ? "Ok" : "No");
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[]{"photoId"}, Map.of(
                "photoId", ((parameter, val) -> !parameter.get("photoId").isEmpty() ? null : "Incorrect photoId parameter.")
        ));
    }
}
