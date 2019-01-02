package dikanev.nikita.core.logic.connector.db.users;

import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.api.users.UserInfo;
import dikanev.nikita.core.logic.connector.db.SQLHelper;
import dikanev.nikita.core.service.server.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class UserDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(UserDBConnector.class);

    private static UserDBConnector ourInstance = new UserDBConnector();

    public static UserDBConnector getInstance() {
        return ourInstance;
    }

    public static UserInfo getInfo(int userId, String login, String email, String... columns) throws SQLException {
        String cols;
        if (columns != null && columns.length != 0) {
            cols = Joiner.on(", ").join(columns);
            cols = cols.replaceAll("userId", "id_user").replaceAll("nameOnGame", "game_name");
        } else {
            cols = "id_user, login, email, age, phone, city, game_name";
        }
        String sql = "SELECT " + cols + " " +
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

    public static Map<Integer, String> insertPhoto(int idUserCreator, int userId, String[] photos) throws SQLException, InvalidParametersException {
        if (photos == null || photos.length == 0) {
            throw new InvalidParametersException("Not found photos");
        }

        String sqlInsertPhoto = "INSERT INTO img SET user_creator = ?, id_owner = ?, link = ?, shown = 'user'";
        String sqlLastIndexId = "SELECT LAST_INSERT_ID() AS id";
        SQLRequest requestLastIndexId = new SQLRequest(DBStorage.getInstance().getConnection()).build(sqlLastIndexId);
        SQLRequest requestInsertPhoto = new SQLRequest(DBStorage.getInstance().getConnection()).build(sqlInsertPhoto)
                                            .set(p -> p.setInt(1, idUserCreator))
                                            .set(p -> p.setInt(2, userId));

        Map<Integer, String> photosIdMap = new HashMap<>(photos.length);

        Arrays.stream(photos).forEach(it -> {
            try {
                int updateRes = requestInsertPhoto.set(p -> p.setString(3, it)).executeUpdate();
                if (updateRes > 0) {
                    int photoId = SQLHelper.moveFirstRow(requestLastIndexId.executeQuery()).getInt("id");
                    photosIdMap.put(photoId, it);
                }
            } catch (SQLException e) {
                LOG.error("Failed insert photo: " + it, e);
            }
        });

        requestInsertPhoto.close();
        requestLastIndexId.close();

        return photosIdMap;
    }

    public static boolean deletePhoto(Integer... photosId) throws SQLException {
        if (photosId == null || photosId.length == 0) {
            throw new IllegalStateException("Not found photoId");
        }

        String sqlImg = "DELETE FROM img WHERE id = ?";
        SQLRequest reqImg = new SQLRequest(DBStorage.getInstance().getConnection()).build(sqlImg);

        for (int photo : photosId) {
            reqImg.set(p -> p.setInt(1, photo)).executeUpdate();
        }

        reqImg.close();
        return true;
    }

    public static Map<Integer, String> getPhoto(Integer[] photosId) throws SQLException {
        if (photosId == null || photosId.length == 0) {
            throw new IllegalStateException("Not found photoId");
        }

        String preparedPhotosId = Joiner.on(", ").join(Arrays.stream(photosId).map(String::valueOf).toArray());
        String sql = "SELECT id, link FROM img WHERE id IN (" + preparedPhotosId + ")";
        SQLRequest reqImg = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);

        ResultSet res = reqImg.executeQuery();
        Map<Integer, String> respPhotos = new HashMap<>(photosId.length);

        while (res.next()) {
            respPhotos.put(res.getInt("id"), res.getString("link"));
        }

        res.close();
        return respPhotos;
    }

    public JsonObject getUserAndUserInfo(int userId, String login, String email, String... columns) throws SQLException {
        String cols;
        if (columns != null) {
            cols = Joiner.on(", ").join(Arrays.stream(columns).map(UserDBConnector::escape).toArray());
            cols = cols.replaceAll("userId", "id")
                    .replaceAll("nameOnGame", "game_name")
                    .replaceAll("groupId", "id_group");
        } else {
            cols = "id, name, s_name, id_group, login, email, age, phone, city, game_name";
        }

        String sql = "SELECT " + cols + " " +
                "FROM users " +
                "JOIN user_info ON id = id_user " +
                "WHERE 0 " + (userId > 0 ? " OR id = ? " : "")
                + (login != null ? " OR login = ? " : "")
                + (email != null ? " OR email = ? " : "");

        PreparedStatement prStatement;
        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
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

        JsonObject js = null;
        while (res.next()) {
            js = new JsonObject();
            js.addProperty("type", "user-information");

            addJsonPropertyFromResultSet(js, res, "userId", "id", "int");
            addJsonPropertyFromResultSet(js, res, "groupId", "id_group", "int");
            addJsonPropertyFromResultSet(js, res, "name", "name", "string");
            addJsonPropertyFromResultSet(js, res, "s_name", "s_name", "string");

            addJsonPropertyFromResultSet(js, res, "login", "login", "string");
            addJsonPropertyFromResultSet(js, res, "email", "email", "string");
            addJsonPropertyFromResultSet(js, res, "age", "age", "date");
            addJsonPropertyFromResultSet(js, res, "phone", "phone", "string");
            addJsonPropertyFromResultSet(js, res, "city", "city", "string");
            addJsonPropertyFromResultSet(js, res, "nameOnGame", "game_name", "string");
        }

        res.close();
        return js;
    }

    private static String escape(String it) {
        try {
            return URLEncoder.encode(it, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Failed escape word.", e);
        }
        return it;
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

        return countUpdate != 0;
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

    private static void addJsonPropertyFromResultSet(JsonObject js, ResultSet res, String prop, String col, String type) throws SQLException {
        if (type == null || (type = type.trim().toLowerCase()).isEmpty() || !isExist(res, col)) {
            return;
        }

        switch (type) {
            case "string":
                js.addProperty(prop, res.getString(col));
                break;
            case "int":
                js.addProperty(prop, res.getInt(col));
                break;
            case "date":
                Date date = res.getDate(col);
                js.addProperty(prop, date != null ? date.toString() : null);
                break;
        }
    }

    private static boolean isExist(ResultSet resultSet, String columnName) throws SQLException {
        if (columnName == null || (columnName = columnName.trim()).isEmpty())
            return false;

        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++)
            if (columnName.equals(metaData.getColumnName(i)))
                return true;

        return false;
    }
}
