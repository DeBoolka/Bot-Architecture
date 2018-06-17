package dikanev.nikita.core.controller.users;

import com.google.common.hash.Hashing;
import dikanev.nikita.core.controller.db.users.UserDBController;
import dikanev.nikita.core.model.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static UserController ourInstance = new UserController();

    private PreparedStatement prStatement;

    public static UserController getInstance() {
        return ourInstance;
    }

    //Создание человека
    public int createUser(String sName, String name, int idGroup) throws SQLException {
        return UserDBController.getInstance().createUser(sName, name, idGroup);
    }

    //Создание человека
    public int createUser(int id, String sName, String name, int idGroup) throws SQLException {
        return UserDBController.getInstance().createUser(id, sName, name, idGroup);
    }

    //Удаление человека
    public boolean deleteUser(int idUser) throws SQLException {
        return UserDBController.getInstance().deleteUser(idUser);
    }

    //Добавление человека в группу
    public boolean toGroup(int idUser, int idGroup) throws SQLException {
        return UserDBController.getInstance().toGroup(idUser, idGroup);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id_group, s_name, name
    public Map<String, Object> getData(int idUser) throws SQLException {
        return UserDBController.getInstance().getData(idUser);
    }

    //Получение информации о человеке.
    //Возвращает map с ключами: id, id_group, s_name, name
    public Map<String, Object> getData(String hash) throws SQLException {
        return UserDBController.getInstance().getData(hash);
    }

    //Создать токен
    public String createToken(int id) throws SQLException {
        return UserDBController.getInstance().createToken(id);
    }

    //Удалить токен
    public boolean deleteToken(int id) throws SQLException {
        return UserDBController.getInstance().deleteToken(id);
    }

    //Получение хеша строки
    public String getHash(String text) {
        return UserDBController.getInstance().getHash(text);
    }
}
