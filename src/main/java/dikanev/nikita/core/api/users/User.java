package dikanev.nikita.core.api.users;

import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.api.groups.Group;
import dikanev.nikita.core.controller.groups.AccessGroupController;
import dikanev.nikita.core.controller.users.UserController;
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
            init(id, 3, "", "");
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
            Map<String, Object> resMap = UserController.getInstance().getData(idUser);
            id = idUser;
            idGroup = (Integer) resMap.get("id_group");
            sName = (String) resMap.get("s_name");
            name = (String) resMap.get("name");
        } catch (SQLException e) {
            LOG.warn(e.getSQLState());
        }
    }

    //Загружает данные из БД
    public void loadFromDB(){
        loadFromDB(id);
    }

    //Загружает данные из БД
    public void loadFromDB(String hash) throws NoAccessException {
        try {
            Map<String, Object> resMap = UserController.getInstance().getData(hash);
            if (resMap == null) {
                throw new NoAccessException("The token was not found.");
            }

            id = (Integer) resMap.get("id");
            idGroup = (Integer) resMap.get("id_group");
            sName = (String) resMap.get("s_name");
            name = (String) resMap.get("name");
        } catch (SQLException e) {
            LOG.warn(e.getSQLState());
        }
    }

    public int getId() {
        return id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    //Проверка доступа по текущей группе
    public boolean hasRightByGroup(int idCommand){
        return Group.hasRight(idGroup, idCommand);
    }

    //Проверка доступа по id
    public static boolean hasRightByUser(int idUser, int idCommand){
        try {
            return AccessGroupController.getInstance().hasAccessUser(idUser, idCommand);
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
            return UserController.getInstance().createToken(id);
        } catch (SQLException ignore) {
        }

        return null;
    }
}
