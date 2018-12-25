package dikanev.nikita.core.logic.connector.db.users;

import com.google.common.hash.Hashing;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.api.users.UserInfo;
import dikanev.nikita.core.logic.connector.db.SQLHelper;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class UserDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(UserDBConnector.class);

    private static UserDBConnector ourInstance = new UserDBConnector();

    public static UserDBConnector getInstance() {
        return ourInstance;
    }

    public static UserInfo getInfo(int userId, String login, String email) throws SQLException {
        String sql = "SELECT id_user, login, email, age, phone, city, game_name " +
                "FROM user_info " +
                "WHERE 0 " + (userId > 0 ? " OR id_user = ? " : "")
                + (login != null ? " OR login = ? " : "")
                + (email != null ? " OR email = ? " : "");


        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        int indexWhere = 1;
        if (userId > 0) {
            prStatement.setInt(indexWhere, userId);
            indexWhere++;
        }
        if (login != null) {
            prStatement.setString(indexWhere, login);
            indexWhere++;
        }
        if (email != null) {
            prStatement.setString(indexWhere, email);
        }

        ResultSet res = prStatement.executeQuery();

        UserInfo userInfo = null;
        while (res.next()) {
            userInfo = new UserInfo()
                    .setUserId(res.getInt("id_user"))
                    .setLogin(res.getString("login"))
                    .setEmail(res.getString("email"))
                    .setAge(res.getDate("age"))
                    .setPhone(res.getString("phone"))
                    .setCity(res.getString("city"))
                    .setNameOnGame(res.getString("game_name"));
        }

        res.close();
        return userInfo;

    }

    public static boolean updateInfo(UserInfo userInfo) throws SQLException {

        String sql = "UPDATE user_info " +
                "SET id_user = " + userInfo.getUserId() +
                (userInfo.getLogin() != null ? ", login = ? " : "") +
                (userInfo.getEmail() != null ? ", email = ? " : "") +
                (userInfo.getAge() != null ? ", age = ? " : "") +
                (userInfo.getPhone() != null ? ", phone = ? " : "") +
                (userInfo.getCity() != null ? ", city = ? " : "") +
                (userInfo.getNameOnGame() != null ? ", game_name = ? " : "") +
                " WHERE id_user = " + userInfo.getUserId();

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        int indexStatement = 1;
        if (userInfo.getLogin() != null) {
            prStatement.setString(indexStatement, userInfo.getLogin());
            indexStatement++;
        }
        if (userInfo.getEmail() != null) {
            prStatement.setString(indexStatement, userInfo.getEmail());
            indexStatement++;
        }
        if (userInfo.getAge() != null) {
            prStatement.setString(indexStatement, userInfo.getAge().toString());
            indexStatement++;
        }
        if (userInfo.getPhone() != null) {
            prStatement.setString(indexStatement, userInfo.getPhone());
            indexStatement++;
        }
        if (userInfo.getCity() != null) {
            prStatement.setString(indexStatement, userInfo.getCity());
            indexStatement++;
        }
        if (userInfo.getNameOnGame() != null) {
            prStatement.setString(indexStatement, userInfo.getNameOnGame());
        }

        int res = prStatement.executeUpdate();
        prStatement.close();

        return res != 0;
    }

    public static boolean updateBaseInfo(User user) throws SQLException {
        String sql = "UPDATE users " +
                "SET id = " + user.getId() +
                (user.getName() != null ? ", name = ? " : "") +
                (user.getsName() != null ? ", s_name = ? " : "") +
                (user.getIdGroup() > 0 ? ", id_group = ? " : "") +
                " WHERE id = " + user.getId();

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        int indexStatement = 1;
        if (user.getName() != null) {
            prStatement.setString(indexStatement, user.getName());
            indexStatement++;
        }
        if (user.getsName() != null) {
            prStatement.setString(indexStatement, user.getsName());
            indexStatement++;
        }
        if (user.getIdGroup() > 0) {
            prStatement.setInt(indexStatement, user.getIdGroup());
        }


        int res = prStatement.executeUpdate();
        prStatement.close();

        return res != 0;
    }

    //Создание человека
    public int registerUser(String email, String login, String sname, String name, int idGroup, String password) throws SQLException {
        String sql = "SELECT REGISTER_USER(?, ?, ?, ?, ?, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, email);
        prStatement.setString(2, login);
        prStatement.setString(3, sname);
        prStatement.setString(4, name);
        prStatement.setInt(5, idGroup);
        prStatement.setString(6, password);

        ResultSet res = prStatement.executeQuery();
        int userId = -1;
        while (res.next()) {
            userId = res.getInt(1);
        }
        res.close();

        return userId;
    }

    //Создание человека
    public int createUser(String sName, String name, int idGroup) throws SQLException {
        String sql = "INSERT INTO users(id_group, s_name, name) " +
                "VALUES (?, ?, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete user with id: " + idUser);
            return false;
        }

        UserDBConnector.getInstance().deleteToken(idUser);

        return true;
    }

    //Добавление человека в группу
    public boolean toGroup(int idUser, int idGroup) throws SQLException {
        String sql = "UPDATE users SET id_group = ? " +
                "WHERE id = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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
