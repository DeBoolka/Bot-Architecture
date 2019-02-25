package dikanev.nikita.core.logic.connector.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.item.Game;
import dikanev.nikita.core.api.item.GameRole;
import dikanev.nikita.core.api.item.Gamer;
import dikanev.nikita.core.api.item.RoleForGame;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.SimpleArrayObject;
import dikanev.nikita.core.service.item.sql.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

        String sql = "SELECT id_game, id_user, user_group, status, id_role, money, filing_time, name, id_organizer, city, game_date " +
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

    public static Game getGame(int gameId) throws SQLException, InvalidParametersException {
        String sql = "SELECT id, name, id_organizer, city, game_date " +
                "FROM games " +
                "WHERE id = ? " +
                "ORDER BY game_date " +
                "LIMIT 1";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.set(p -> p.setInt(1, gameId));

        ResultSet res = req.executeQuery();
        if (!res.next()) {
            throw new InvalidParametersException("Not found game id");
        }
        Game game = getGameFromResultSet(res);

        res.close();
        req.close();
        return game;
    }

    public static boolean isUserSignedUpToGame(int gameId, int userId) throws SQLException {
        String sql = "SELECT EXISTS(" +
                "SELECT id_game, id_user " +
                "FROM gamers " +
                "WHERE id_game = ? AND id_user = ? " +
                "LIMIT 1" +
                ") AS signed";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.setInt(1, gameId).setInt(2, userId);

        ResultSet res = req.executeQuery();
        boolean isSigned = false;
        if (res.next()) {
            isSigned = res.getBoolean("signed");
        }

        res.close();
        req.close();
        return isSigned;
    }

    public static RoleForGame[] getRolesFromTheGame(int gameId) throws SQLException {
        String sql = "SELECT id_role, name, description, id_game " +
                "   , (user_max_count - NUMBER_OF_OCCUPIED_SEATS_FOR_THE_GAME(id_game, id_role)) AS number_of_available_seats " +
                "   , user_max_count, armored_max_count " +
                "FROM game_roles " +
                "   JOIN roles_for_game ON id_role = game_roles.id " +
                "WHERE id_game = ?";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.setInt(1, gameId);

        ResultSet res = req.executeQuery();
        List<RoleForGame> roles = new ArrayList<>();
        while (res.next()) {
            roles.add(getRoleForGameFromResultSet(res));
        }

        res.close();
        req.close();
        return roles.toArray(new RoleForGame[0]);
    }

    public static RoleForGame getRoleFromTheGame(int gameId, int roleId) throws SQLException {
        String sql = "SELECT id_role, name, description, id_game " +
                "   , (user_max_count - NUMBER_OF_OCCUPIED_SEATS_FOR_THE_GAME(id_game, id_role)) AS number_of_available_seats " +
                "   , user_max_count, armored_max_count " +
                "FROM game_roles " +
                "   JOIN roles_for_game ON id_role = game_roles.id " +
                "WHERE id_game = ? AND id_role = ? " +
                "LIMIT 1";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.setInt(1, gameId).setInt(2, roleId);

        ResultSet res = req.executeQuery();
        RoleForGame role = null;
        if (res.next()) {
            role = getRoleForGameFromResultSet(res);
        }

        res.close();
        req.close();
        return role;
    }

    public static Gamer registerUserToGame(int userId, int gameId, int roleId) throws SQLException, NotFoundException {
        String sql = "SELECT REGISTER_USER_TO_GAME(?, ?, ?)";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.setInt(1, userId)
                .setInt(2, gameId)
                .setInt(3, roleId);

        req.executeQuery().close();
        req.close();

        return getGamer(userId, gameId);
    }

    private static Gamer getGamer(int userId, int gameId) throws SQLException, NotFoundException {
        String sql = "SELECT id_game, id_user, user_group, status, id_role, money, filing_time " +
                "FROM gamers " +
                "WHERE id_user = ? AND id_game = ? " +
                "LIMIT 1";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);
        req.setInt(1, userId)
                .setInt(2, gameId);

        ResultSet res = req.executeQuery();
        Gamer gamer = null;
        if (res.next()) {
            gamer = getGamerFromResultSet(res);
        }

        res.close();
        req.close();
        if (gamer == null) {
            throw new NotFoundException("User Not Found");
        }
        return gamer;
    }

    private static Gamer getGamerFromResultSet(ResultSet res) throws SQLException {
        int gameId = res.getInt("id_game");
        int userId = res.getInt("id_user");
        String userGroup = res.getString("user_group");
        String status = res.getString("status");
        int roleId = res.getInt("id_role");
        int money = res.getInt("money");
        Timestamp fillingTime = res.getTimestamp("filing_time");
        return new Gamer(gameId, userId, userGroup, status, roleId, money, fillingTime);
    }

    private static RoleForGame getRoleForGameFromResultSet(ResultSet res) throws SQLException {
        int gameRoleId = res.getInt("id_role");
        String gameRoleName = res.getString("name");
        String gameRoleDescription = res.getString("description");

        GameRole gameRole = new GameRole(gameRoleId, gameRoleName, gameRoleDescription);
        int numberOfAvailableSeats = res.getInt("number_of_available_seats");
        int userMaxCount = res.getInt("user_max_count");
        int armoredMaxCount = res.getInt("armored_max_count");
        return new RoleForGame(gameRole, numberOfAvailableSeats, userMaxCount, armoredMaxCount);
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
        Gamer gamerObj = getGamerFromResultSet(res);
        gamer.addProperty("userId", gamerObj.userId);
        gamer.addProperty("userGroup", gamerObj.userGroup);
        gamer.addProperty("status", gamerObj.status);
        gamer.addProperty("roleId", gamerObj.roleId);
        gamer.addProperty("money", gamerObj.money);
        gamer.addProperty("fillingTime", gamerObj.fillingTime.toString());
        return gamer;
    }

    private static JsonObject getJsonGameFromResultSet(ResultSet res) throws SQLException {
        JsonObject game = new JsonObject();
        game.addProperty("id", res.getInt("id_game"));
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
