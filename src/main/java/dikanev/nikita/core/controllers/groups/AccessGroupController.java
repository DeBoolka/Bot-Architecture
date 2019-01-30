package dikanev.nikita.core.controllers.groups;

import dikanev.nikita.core.controllers.commands.CommandController;
import dikanev.nikita.core.logic.connector.db.groups.AccessGroupDBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AccessGroupController {
    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupController.class);

    //Устанавливает доступ к команде для группы
    public static boolean createAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        return AccessGroupDBConnector.getInstance().createAccess(idGroup, idCommand, privilege);
    }

    //Устанавливает доступ к командам для группы
    public static boolean createAccess(int idGroup, int[] idCommands, boolean privilege) throws SQLException {
        return AccessGroupDBConnector.getInstance().createAccess(idGroup, idCommands, privilege);
    }

    public static boolean createAccess(int idGroup, String command, boolean privilege) throws SQLException{
        return AccessGroupDBConnector.getInstance().createAccess(idGroup, command, privilege);
    }

    public static boolean createAccess(int idGroup, String[] nameCommands, boolean privilege) throws SQLException {
        return AccessGroupDBConnector.getInstance().createAccess(idGroup, nameCommands, privilege);
    }

    //Проверяет доступна ли комманда пользователю
    public static boolean hasAccessUser(int idUser, int idCommand) throws SQLException {
        return AccessGroupDBConnector.getInstance().hasAccessUser(idUser, idCommand);
    }

    //Проверяет доступна ли комманда группе
    public static boolean hasAccessGroup(int idGroup, int idCommand) throws SQLException {
        String commandName = CommandController.getInstance().getName(idCommand);
        if (commandName != null) {
            return AccessGroupDBConnector.getInstance().hasAccessGroup(idGroup, commandName);
        }
        return false;
    }

    //Проверяет доступна ли комманда группе
    public static boolean hasAccessGroup(int idGroup, String commandName) throws SQLException {
        return AccessGroupDBConnector.getInstance().hasAccessGroup(idGroup, commandName);
    }

    //Возвращает доступность команд для группы
    public static Map<String, Boolean> getAccessGroup(int idGroup, List<String> commandsName) throws SQLException {
        return AccessGroupDBConnector.getInstance().getAccessGroup(idGroup, commandsName);
    }

    //Изменяет доступ к команде для группы
    public static boolean editAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        return AccessGroupDBConnector.getInstance().editAccess(idGroup, idCommand, privilege);
    }

    //Удаляет из БД запись с доступом к команде
    public static boolean deleteAccess(int idGroup, int idCommand) throws SQLException {
        return AccessGroupDBConnector.getInstance().deleteAccess(idGroup, idCommand);
    }
}
