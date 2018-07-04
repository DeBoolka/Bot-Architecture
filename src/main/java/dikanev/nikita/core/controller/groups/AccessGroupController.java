package dikanev.nikita.core.controller.groups;

import dikanev.nikita.core.controller.commands.CommandController;
import dikanev.nikita.core.controller.db.groups.AccessGroupDBController;
import dikanev.nikita.core.model.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessGroupController {
    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupController.class);

    private static AccessGroupController ourInstance = new AccessGroupController();

    private PreparedStatement prStatement;

    public static AccessGroupController getInstance() {
        return ourInstance;
    }

    //Устанавливает доступ к команде для группы
    public boolean createAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        return AccessGroupDBController.getInstance().createAccess(idGroup, idCommand, privilege);
    }

    //Устанавливает доступ к командам для группы
    public boolean createAccess(int idGroup, Integer[] idCommands, boolean privilege) throws SQLException {
        return AccessGroupDBController.getInstance().createAccess(idGroup, idCommands, privilege);
    }

    public boolean createAccess(int idGroup, String command, boolean privilege) throws SQLException{
        return AccessGroupDBController.getInstance().createAccess(idGroup, command, privilege);
    }

    //Проверяет доступна ли комманда пользователю
    public boolean hasAccessUser(int idUser, int idCommand) throws SQLException {
        return AccessGroupDBController.getInstance().hasAccessUser(idUser, idCommand);
    }

    //Проверяет доступна ли комманда группе
    public boolean hasAccessGroup(int idGroup, int idCommand) throws SQLException {
        String commandName = CommandController.getInstance().getName(idCommand);
        if (commandName != null) {
            return AccessGroupDBController.getInstance().hasAccessGroup(idGroup, commandName);
        }
        return false;
    }

    //Проверяет доступна ли комманда группе
    public boolean hasAccessGroup(int idGroup, String commandName) throws SQLException {
        return AccessGroupDBController.getInstance().hasAccessGroup(idGroup, commandName);
    }

    //Изменяет доступ к команде для группы
    public boolean editAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        return AccessGroupDBController.getInstance().editAccess(idGroup, idCommand, privilege);
    }

    //Удаляет из БД запись с доступом к команде
    public boolean deleteAccess(int idGroup, int idCommand) throws SQLException {
        return AccessGroupDBController.getInstance().deleteAccess(idGroup, idCommand);
    }
}
