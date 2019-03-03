package dikanev.nikita.core.controllers.users;

import com.google.gson.JsonObject;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.api.users.UserInfo;
import dikanev.nikita.core.logic.connector.db.users.UserDBConnector;
import org.checkerframework.checker.nullness.compatqual.NonNullType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    public static UserInfo getInfo(int userId, String... columns) throws SQLException {
        return UserDBConnector.getInfo(userId, null, null, columns);
    }

    public static UserInfo getInfoByLogin(String login, String... columns) throws SQLException {
        return UserDBConnector.getInfo(-1, login, null, columns);
    }

    public static UserInfo getInfoByEmail(String email, String... columns) throws SQLException {
        return UserDBConnector.getInfo(-1, null, email, columns);
    }

    public static boolean updateInfo(UserInfo userInfo) throws SQLException {
        return UserDBConnector.updateInfo(userInfo);
    }

    public static boolean updateBaseInfo(User user) throws SQLException {
        return UserDBConnector.updateBaseInfo(user);
    }

    public static JsonObject getUserAndUserInfo(int userId, String[] columns) throws SQLException {
        return UserDBConnector.getInstance().getUserAndUserInfo(userId, null, null, columns);
    }

    public static JsonObject getUserAndUserInfoByLogin(String login, String[] columns) throws SQLException {
        return UserDBConnector.getInstance().getUserAndUserInfo(-1, login, null, columns);
    }

    public static JsonObject getUserAndUserInfoByEmail(String email, String[] columns) throws SQLException {
        return UserDBConnector.getInstance().getUserAndUserInfo(-1, null, email, columns);
    }

    public static Map<Integer, String> insertPhoto(int userId, String... photos) throws SQLException, InvalidParametersException {
        return UserDBConnector.insertPhoto(userId, userId, photos);
    }

    public static boolean deletePhoto(@NonNullType Integer... photoId) throws SQLException {
        return UserDBConnector.deletePhoto(photoId);
    }

    public static Map<Integer, String> getPhoto(Integer[] photosId) throws SQLException {
        return UserDBConnector.getPhoto(photosId);
    }

    public static Map<Integer, String> getPhotoByUser(int userId, int indentPhoto, int countPhoto) throws SQLException {
        return UserDBConnector.getPhotoByUser(userId, indentPhoto, countPhoto);
    }
    //Создание человека
    public static int registerUser(String email, String login, String sname, String name, int idGroup, String password) throws SQLException {
        return UserDBConnector.getInstance().registerUser(email, login, sname, name, idGroup, password);
    }

    //Создание человека
    public static int createUser(String sName, String name, int idGroup) throws SQLException {
        return UserDBConnector.getInstance().createUser(sName, name, idGroup);
    }

    //Создание человека
    public static int createUser(int id, String sName, String name, int idGroup) throws SQLException {
        return UserDBConnector.getInstance().createUser(id, sName, name, idGroup);
    }

    //Удаление человека
    public static boolean deleteUser(int idUser) throws SQLException {
        return UserDBConnector.getInstance().deleteUser(idUser);
    }

    //Добавление человека в группу
    public static boolean toGroup(int idUser, int idGroup) throws SQLException {
        return UserDBConnector.getInstance().toGroup(idUser, idGroup);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id_group, s_name, name
    public static Map<String, Object> getData(int idUser) throws SQLException {
        return UserDBConnector.getInstance().getData(idUser);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_group, s_name, name
    public static Map<String, Object> getData(String hash) throws SQLException {
        return UserDBConnector.getInstance().getData(hash);
    }

    //Создать токен
    public static String createToken(int id) throws SQLException {
        return UserDBConnector.getInstance().createToken(id);
    }

    //Удалить токен
    public static boolean deleteToken(int id) throws SQLException {
        return UserDBConnector.getInstance().deleteToken(id);
    }

    //Получение хеша строки
    public static String getHash(String text) {
        return UserDBConnector.getInstance().getHash(text);
    }

    public static int getGroupId(int userId) throws SQLException {
        return UserDBConnector.getInstance().getGroup(userId);
    }
}
