package dikanev.nikita.core.logic.commands.game;

import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.item.Game;
import dikanev.nikita.core.api.objects.*;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.GameController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.item.parameter.Parameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GameCommand extends Command {
    private final static int COUNT_GAMES = 5;

    public GameCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject ChoiceStartPointOfWork(User user, Parameter params, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        switch (commandParser.headers.getF(CommandParser.ENTER_METHOD).toLowerCase()) {
            case "get":
                switch (commandParser.headers.getFOrDefault("O-FOR", "")) {
                    case "ALL":
                        return getGames(params);
                    case "USER":
                        return getSignedUpForTheGame(params);
                    case "GAME":
                        return getGame(params);
                }
            default:
                return super.ChoiceStartPointOfWork(user, params, commandParser);
        }
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        return null;
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return new PreparedParameter(new String[][]{
                new String[]{"userId", "name"},
                new String[]{"gameId"},
                new String[0]
        }, Map.of(
                "userId", ((parameter, val) -> parameter.isIntF("userId") ? null : "Incorrect userId parameter."),
                "name", ((parameter, val) -> parameter.getF("name").length() > 127 || parameter.getF("name").isEmpty() ? "Incorrect name parameter." : null),
                "gameId", ((parameter, val) -> parameter.isIntF("gameId") ? null : "Incorrect gameId parameter.")
                ));

    }

    private ApiObject getGames(Parameter params) throws InvalidParametersException, SQLException {
        int count;
        int indent;
        try {
            count = params.getIntFOrDefault("count", COUNT_GAMES);
            indent = params.getIntFOrDefault("indent", 0);
        } catch (Exception e) {
            throw new InvalidParametersException("Incorrect count or indent parameter.");
        }

        List<Game> games = GameController.getGames(indent, count);
        return new SimpleArrayObject(games);
    }

    private ApiObject getSignedUpForTheGame(Parameter params) throws InvalidParametersException, SQLException {
        int userId;
        int count;
        int indent;
        try {
            userId = params.getIntF("userId");
            count = params.getIntFOrDefault("count", COUNT_GAMES);
            indent = params.getIntFOrDefault("indent", 0);
        } catch (Exception e) {
            throw new InvalidParametersException("Incorrect userId, count or indent parameter.");
        }

        return GameController.getUserSignedUpForTheGame(userId, indent, count);

    }

    private ApiObject getGame(Parameter params) throws NoSuchFieldException, InvalidParametersException, SQLException {
        int gameId = params.getIntF("gameId");
        if (gameId <= 0) {
            return new ExceptionObject(new InvalidParametersException("Incorrect gameId parameter."));
        }

        return new SimpleObject(SimpleObject.GAME, GameController.getGame(gameId));
    }
}
