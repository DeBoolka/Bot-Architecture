package dikanev.nikita.core.controllers;

import dikanev.nikita.core.api.item.Game;
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
}
