package dikanev.nikita.core.logic.connector.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.item.Game;
import dikanev.nikita.core.api.objects.SimpleArrayObject;
import dikanev.nikita.core.service.item.sql.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDBConnector {
    public static List<Game> getGames(int indent, int count) throws SQLException {
        if (indent < 0 || count <= 0) {
            throw new IllegalStateException("Invalid indent or count parameter");
        }

        String sql = "SELECT id, name, id_organizer, city, game_date " +
                "FROM games " +
                "WHERE game_date IS NOT NULL AND game_date > NOW() " +
                "ORDER BY game_date " +
                "LIMIT ?, ?";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.set(p -> {
            p.setInt(1, indent);
            p.setInt(2, count);
        });

        List<Game> games = new ArrayList<>();
        ResultSet res = req.executeQuery();
        while (res.next()) {
            games.add(getGameFromResultSet(res));
        }

        res.close();
        req.close();
        return games;
    }

    public static SimpleArrayObject getUserSignedUpForTheGame(int userId, int indent, int count) throws SQLException {
        if (indent < 0 || count <= 0) {
            throw new IllegalStateException("Invalid indent or count parameter");
        }

        String sql = "SELECT id_game, id_user, user_group, status, id_role, filing_time, name, id_organizer, city, game_date " +
                "FROM games " +
                " JOIN gamers ON id = id_game " +
                "WHERE id_user = ? AND game_date > NOW() " +
                "ORDER BY game_date " +
                "LIMIT ?, ?";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.set(p -> {
            p.setInt(1, userId);
            p.setInt(2, indent);
            p.setInt(3, count);
        });

        ResultSet res = req.executeQuery();
        JsonArray games = getJsonArrayGameAndGamerFromResultSet(res);

        res.close();
        req.close();
        return new SimpleArrayObject(games);
    }

    private static JsonArray getJsonArrayGameAndGamerFromResultSet(ResultSet res) throws SQLException {
        JsonArray games = new JsonArray();
        while (res.next()) {
            JsonObject game = getJsonGameFromResultSet(res);
            JsonObject gamer = getJsonGamerFromResultSet(res);

            JsonObject jsObjectIntoJsArray = new JsonObject();
            jsObjectIntoJsArray.add("game", game);
            jsObjectIntoJsArray.add("gamer", gamer);

            games.add(jsObjectIntoJsArray);
        }
        return games;
    }

    private static JsonObject getJsonGamerFromResultSet(ResultSet res) throws SQLException {
        JsonObject gamer = new JsonObject();
        gamer.addProperty("userId", res.getInt("id_user"));
        gamer.addProperty("userGroup", res.getString("user_group"));
        gamer.addProperty("status", res.getString("status"));
        gamer.addProperty("roleId", res.getInt("id_role"));
        gamer.addProperty("fillingTime", res.getTimestamp("filing_time").toString());
        return gamer;
    }

    private static JsonObject getJsonGameFromResultSet(ResultSet res) throws SQLException {
        JsonObject game = new JsonObject();
        game.addProperty("id", res.getInt("id_user"));
        game.addProperty("name", res.getString("name"));
        game.addProperty("organizerId", res.getInt("id_organizer"));
        game.addProperty("city", res.getString("city"));
        game.addProperty("gameDate", res.getTimestamp("game_date").toString());
        return game;
    }

    private static Game getGameFromResultSet(ResultSet res) throws SQLException {
        return new Game(
                res.getInt("id"),
                res.getString("name"),
                res.getInt("id_organizer"),
                res.getString("city"),
                res.getTimestamp("game_date")
        );
    }
}
