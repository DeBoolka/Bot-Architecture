package dikanev.nikita.core.controllers;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.item.Game;
import dikanev.nikita.core.api.item.Gamer;
import dikanev.nikita.core.api.item.RoleForGame;
import dikanev.nikita.core.api.objects.SimpleArrayObject;
import dikanev.nikita.core.logic.connector.db.GameDBConnector;

import java.sql.SQLException;
import java.util.List;

public class GameController {
    public static List<Game> getGames(int indent, int count) throws SQLException {
        return GameDBConnector.getGames(indent, count);
    }

    /**
     *
     * @param userId - userId
     * @param indent - отступ от начала списка
     * @param count - количество возвращаемых элементов
     * @return {@code SimpleArrayObject} содержащий в себе массив объектов {@code Game} и {@code Gamer}
     * @throws SQLException - при возникновении исключении в базе данных
     * @throws IllegalStateException - при неверных параметрах
     */
    public static SimpleArrayObject getUserSignedUpForTheGame(int userId, int indent, int count) throws SQLException {
        return GameDBConnector.getUserSignedUpForTheGame(userId, indent, count);
    }

    public static Game getGame(int gameId) throws InvalidParametersException, SQLException {
        return GameDBConnector.getGame(gameId);
    }

    public static boolean isUserSignedUpToGame(int gameId, int userId) throws SQLException {
        return GameDBConnector.isUserSignedUpToGame(gameId, userId);
    }

    public static RoleForGame[] getRolesFromTheGame(int gameId) throws SQLException {
        return GameDBConnector.getRolesFromTheGame(gameId);
    }

    public static RoleForGame getRoleFromTheGame(int gameId, int roleId) throws SQLException {
        return GameDBConnector.getRoleFromTheGame(gameId, roleId);
    }

    public static Gamer registerUserToGame(int userId, int gameId, int roleId) throws SQLException, NotFoundException {
        return GameDBConnector.registerUserToGame(userId, gameId, roleId);
    }
}
