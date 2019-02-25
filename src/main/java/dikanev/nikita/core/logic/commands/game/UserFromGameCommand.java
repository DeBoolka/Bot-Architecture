package dikanev.nikita.core.logic.commands.game;

import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.item.Gamer;
import dikanev.nikita.core.api.item.RoleForGame;
import dikanev.nikita.core.api.objects.*;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.GameController;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.item.parameter.Parameter;
import dikanev.nikita.core.service.server.CommandParser;

import java.sql.SQLException;

public class UserFromGameCommand extends Command {
    public UserFromGameCommand(int id) {
        super(id);
    }

    @Override
    protected ApiObject work(User user, Parameter params) {
        return null;
    }

    @Override
    protected PreparedParameter setupParameters(Parameter params) {
        return null;
    }

    @Override
    protected ApiObject ChoiceStartPointOfWork(User user, Parameter params, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        switch (commandParser.headers.getF(CommandParser.ENTER_METHOD).toLowerCase()) {
            case "register":
                return signUpUserToGame(params);
            case "issigned":
                return isUserSignedUpToGame(params);
            case "get":
                switch (commandParser.headers.getFOrDefault("O-FOR", "")) {
                    case "ROLES":
                        return getRoles(params);
                    case "ROLE":
                        return getRole(params);
                }
            case "check":
                return checkTestCommand(params);
            default:
                return super.ChoiceStartPointOfWork(user, params, commandParser);
        }
    }

    private ApiObject getRoles(Parameter params) throws NoSuchFieldException, SQLException, InvalidParametersException {
        if (!params.contains("gameId") || !params.isIntF("gameId")) {
            throw new InvalidParametersException("Invalid gameId parameter.");
        }

        int gameId = params.getIntF("gameId");
        RoleForGame[] roles = GameController.getRolesFromTheGame(gameId);
        return new SimpleArrayObject(roles);
    }

    private ApiObject getRole(Parameter params) throws InvalidParametersException, NoSuchFieldException, SQLException {
        if (!params.contains("gameId", "roleId")
                || !params.isIntF("gameId")
                || !params.isIntF("roleId")) {
            throw new InvalidParametersException("Invalid gameId or roleId parameter.");
        }

        int gameId = params.getIntF("gameId");
        int roleId = params.getIntF("roleId");
        RoleForGame role = GameController.getRoleFromTheGame(gameId, roleId);
        return new SimpleObject("roleForGame", role);
    }

    private ApiObject signUpUserToGame(Parameter params) throws InvalidParametersException, NoSuchFieldException, NotFoundException, SQLException {
        if (!params.contains("gameId", "roleId", "userId")
                || !params.isIntF("gameId")
                || !params.isIntF("roleId")
                || !params.isIntF("userId")) {
            throw new InvalidParametersException("Invalid gameId, roleId or userId parameter.");
        }

        int userId = params.getIntF("userId");
        int gameId = params.getIntF("gameId");
        int roleId = params.getIntF("roleId");
        Gamer gamer = GameController.registerUserToGame(userId, gameId, roleId);
        return new SimpleObject("gamer", gamer);
    }

    private ApiObject isUserSignedUpToGame(Parameter params) throws NoSuchFieldException, SQLException {
        if (!params.isIntF("userId") || !params.isIntF("gameId")) {
            return new ExceptionObject(new InvalidParametersException("Invalid userId or gameId parameter."));
        }

        int userId = params.getIntF("userId");
        int gameId = params.getIntF("gameId");
        return new MessageObject(GameController.isUserSignedUpToGame(gameId, userId) ? "true" : "false");
    }

    private ApiObject checkTestCommand(Parameter params) {
        //todo: Сделать! Это просто заглушка
        return new MessageObject("true");
    }
}
