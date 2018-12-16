package dikanev.nikita.core.controllers.users;

import dikanev.nikita.core.api.users.UserInfo;
import dikanev.nikita.core.logic.connector.db.users.UserDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static UserController ourInstance = new UserController();

    private PreparedStatement prStatement;

    public static UserController getInstance() {
        return ourInstance;
    }

    public static UserInfo getInfo(int userId) throws SQLException {
        return UserDBConnector.getInfo(userId, null, null);
    }

    public static UserInfo getInfoByLogin(String login) throws SQLException {
        return UserDBConnector.getInfo(-1, login, null);
    }

    public static UserInfo getInfoByEmail(String email) throws SQLException {
        return UserDBConnector.getInfo(-1, null, email);
    }

    public static boolean updateInfo(UserInfo userInfo) throws SQLException {
        return UserDBConnector.updateInfo(userInfo);
    }

    //Создание человека
    public int registerUser(String email, String sname, String name, int idGroup, String password) throws SQLException {
        return UserDBConnector.getInstance().registerUser(email, sname, name, idGroup, password);
    }

    //Создание человека
    public int createUser(String sName, String name, int idGroup) throws SQLException {
        return UserDBConnector.getInstance().createUser(sName, name, idGroup);
    }

    //Создание человека
    public int createUser(int id, String sName, String name, int idGroup) throws SQLException {
        return UserDBConnector.getInstance().createUser(id, sName, name, idGroup);
    }

    //Удаление человека
    public boolean deleteUser(int idUser) throws SQLException {
        return UserDBConnector.getInstance().deleteUser(idUser);
    }

    //Добавление человека в группу
    public boolean toGroup(int idUser, int idGroup) throws SQLException {
        return UserDBConnector.getInstance().toGroup(idUser, idGroup);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id_group, s_name, name
    public Map<String, Object> getData(int idUser) throws SQLException {
        return UserDBConnector.getInstance().getData(idUser);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_group, s_name, name
    public Map<String, Object> getData(String hash) throws SQLException {
        return UserDBConnector.getInstance().getData(hash);
    }

    //Создать токен
    public String createToken(int id) throws SQLException {
        return UserDBConnector.getInstance().createToken(id);
    }

    //Удалить токен
    public boolean deleteToken(int id) throws SQLException {
        return UserDBConnector.getInstance().deleteToken(id);
    }

    //Получение хеша строки
    public String getHash(String text) {
        return UserDBConnector.getInstance().getHash(text);
    }
}
