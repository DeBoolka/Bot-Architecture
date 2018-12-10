package dikanev.nikita.core.controllers.db.users;

import com.google.common.hash.Hashing;
import dikanev.nikita.core.controllers.db.SQLHelper;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class UserDBController {

    private static final Logger LOG = LoggerFactory.getLogger(UserDBController.class);

    private static UserDBController ourInstance = new UserDBController();

    private PreparedStatement prStatement;

    public static UserDBController getInstance() {
        return ourInstance;
    }

    //Создание человека
    public int registerUser(String email, String sname, String name, int idGroup, String password) throws SQLException {
        String sql = "SELECT REGISTER_USER(?, ?, ?, ?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, email);
        prStatement.setString(2, sname);
        prStatement.setString(3, name);
        prStatement.setInt(4, idGroup);
        prStatement.setString(5, password);

        ResultSet res = prStatement.executeQuery();
        int userId = -1;
        while (res.next()) {
            userId = res.getInt(1);
        }

        return userId;
    }

    //Создание человека
    public int createUser(String sName, String name, int idGroup) throws SQLException {
        String sql = "INSERT INTO users(id_group, s_name, name) " +
                "VALUES (?, ?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setString(2, sName);
        prStatement.setString(3, name);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create a user with the data: (" + sName + ", " + name + ", " + idGroup + " )");
            throw new IllegalStateException("Failed to create a user");
        }

        return SQLHelper.getLastId();
    }

    //Создание человека
    public int createUser(int id, String sName, String name, int idGroup) throws SQLException {
        String sql = "INSERT INTO users(id_group, s_name, name, id) " +
                "VALUES (?, ?, ?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setString(2, sName);
        prStatement.setString(3, name);
        prStatement.setInt(4, id);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create a user with the data: (" + sName + ", " + name + ", " + idGroup + " )");
            throw new IllegalStateException("Failed to create a user");
        }

        return id;
    }

    //Удаление человека
    public boolean deleteUser(int idUser) throws SQLException {
        String sql = "DELETE FROM users " +
                "WHERE id = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete user with id: " + idUser);
            return false;
        }

        UserDBController.getInstance().deleteToken(idUser);

        return true;
    }

    //Добавление человека в группу
    public boolean toGroup(int idUser, int idGroup) throws SQLException {
        String sql = "UPDATE users SET id_group = ? " +
                "WHERE id = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setInt(2, idUser);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            return false;
        }

        return true;
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id_group, s_name, name
    public Map<String, Object> getData(int idUser) throws SQLException {
        String sql = "SELECT id_group, name, s_name " +
                "FROM users " +
                "WHERE id = ? " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        ResultSet res = prStatement.executeQuery();

        Map<String, Object> resMap = new TreeMap<>();
        while (res.next()) {
            resMap.put("id_group", res.getInt("id_group"));
            resMap.put("s_name", res.getString("s_name"));
            resMap.put("name", res.getString("name"));
        }

        res.close();
        return resMap;
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_group, s_name, name
    public Map<String, Object> getData(String hash) throws SQLException {
        String sql = "SELECT users.id, users.id_group, users.s_name, users.name " +
                "FROM (SELECT tokens.id FROM tokens WHERE token = ? LIMIT 1) AS tkn LEFT JOIN users USING(id) " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, hash);
        ResultSet res = prStatement.executeQuery();

        Map<String, Object> resMap = new TreeMap<>();
        while (res.next()) {
            resMap.put("id", res.getInt("id"));
            resMap.put("id_group", res.getInt("id_group"));
            resMap.put("s_name", res.getString("s_name"));
            resMap.put("name", res.getString("name"));
        }

        res.close();
        return resMap.size() > 0 ? resMap : null;
    }

    //Создать токен
    public String createToken(int id) throws SQLException {
        String hash = getHash(String.valueOf(id) + String.valueOf(System.nanoTime()) + "salt");

        String sql = "INSERT INTO tokens(token, id) " +
                "VALUES (?, ?)";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, hash);
        prStatement.setInt(2, id);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create a token with the data: (" + hash + ", " + id + ")");
            throw new IllegalStateException("Failed to create a token");
        }

        return hash;
    }

    //Удалить токен
    public boolean deleteToken(int id) throws SQLException {
        String sql = "DELETE FROM tokens " +
                "WHERE id = ?";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        int countUpdate = prStatement.executeUpdate();
        prStatement.close();

        if (countUpdate == 0) {
            LOG.warn("Failed to create a token with the data: (" + id + ")");
            return false;
        }

        return true;
    }

    //Получение хеша строки
    public String getHash(String text) {
        return Hashing.sha256().hashString(text, StandardCharsets.UTF_8).toString();
    }

    public int getGroup(int idUser) throws SQLException {
        String sql = "SELECT id_group " +
                "FROM users " +
                "WHERE id = ? " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        ResultSet res = prStatement.executeQuery();

        int idGroup = -1;
        while (res.next()) {
            idGroup = res.getInt("id_group");
        }
        res.close();

        return idGroup;
    }
}
