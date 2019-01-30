package dikanev.nikita.core.api.users;

import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.api.groups.Group;
import dikanev.nikita.core.controllers.groups.AccessGroupController;
import dikanev.nikita.core.controllers.users.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class User {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    public static final int DEFAULT_ID = 2;

    public static final int DEFAULT_GROUP = 3;

    private int id;

    private int idGroup;

    private String sName;

    private String name;

    public User() {
    }

    public User(int id, int idGroup, String sName, String name) {
        init(id, idGroup, sName, name);
    }

    public User(int id, int idGroup) {
        init(id, idGroup, "", "");
    }

    public User(int id, boolean loadBd) {
        if (loadBd) {
            loadFromDB(id);
        } else {
            init(id, DEFAULT_GROUP, "", "");
        }
    }

    public User(String hash) throws NoAccessException {
        loadFromDB(hash);
    }

    public void init(int id, int idGroup, String sName, String name) {
        this.id = id;
        this.idGroup = idGroup;
        this.sName = sName;
        this.name = name;
    }

    //Загружает данные из БД
    public void loadFromDB(int idUser) {
        try {
            Map<String, Object> resMap = UserController.getData(idUser);
            id = idUser;
            idGroup = (Integer) resMap.get("id_group");
            sName = (String) resMap.get("s_name");
            name = (String) resMap.get("name");
        } catch (SQLException e) {
            LOG.warn("Error in loadFromDB: ", e);
        }
    }

    //Загружает данные из БД
    public void loadFromDB(){
        loadFromDB(id);
    }

    //Загружает данные из БД
    public void loadFromDB(String hash) throws NoAccessException {
        try {
            Map<String, Object> resMap = UserController.getData(hash);
            if (resMap == null) {
                throw new NoAccessException("The token was not found.");
            }

            id = (Integer) resMap.get("id");
            idGroup = (Integer) resMap.get("id_group");
            sName = (String) resMap.get("s_name");
            name = (String) resMap.get("name");
        } catch (SQLException e) {
            LOG.warn("Error in loadFromDB: ", e);
        }
    }

    public int getId() {
        return id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getsName() {
        return sName;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Проверка доступа по текущей группе
    public boolean hasRightByGroup(int idCommand){
        return Group.hasRight(idGroup, idCommand);
    }

    //Проверка доступа по id
    public static boolean hasRightByUser(int idUser, int idCommand){
        try {
            return AccessGroupController.hasAccessUser(idUser, idCommand);
        } catch (SQLException ignore) {
        }

        return false;
    }

    //Проверка доступа по текущему id
    public boolean hasRightByUser(int idCommand) {
        return hasRightByUser(id, idCommand);
    }

    //Создание токена
    public String createToken() {
        try {
            return UserController.createToken(id);
        } catch (SQLException ignore) {
        }

        return null;
    }
}
