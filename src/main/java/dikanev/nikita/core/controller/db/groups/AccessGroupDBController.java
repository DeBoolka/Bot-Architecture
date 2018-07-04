package dikanev.nikita.core.controller.db.groups;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.controller.db.commands.CommandDBController;
import dikanev.nikita.core.controller.db.users.UserDBController;
import dikanev.nikita.core.controller.users.UserController;
import dikanev.nikita.core.model.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessGroupDBController {

    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupDBController.class);

    private static AccessGroupDBController ourInstance = new AccessGroupDBController();

    private PreparedStatement prStatement;

    public static AccessGroupDBController getInstance() {
        return ourInstance;
    }

    //Устанавливает доступ к команде для группы
    public boolean createAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        String sql = "INSERT INTO groups_privilege(id_group, id_command, access) " +
                "VAlUES(?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "   access = ?";


        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setInt(2, idCommand);
        prStatement.setBoolean(3, privilege);
        prStatement.setBoolean(4, privilege);
        int res = prStatement.executeUpdate();

        prStatement.close();
        return res > 0;
    }

    //Устанавливает доступ к командам для группы
    public boolean createAccess(int idGroup, Integer[] idCommands, boolean privilege) throws SQLException {
        String sql = "INSERT INTO groups_privilege(id_group, id_command, access) " +
                "VAlUES(?, ?, ?);";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setBoolean(3, privilege);

        int res = 0;
        for (int idCommand : idCommands) {
            prStatement.setInt(2, idCommand);
            res += prStatement.executeUpdate();
        }

        prStatement.close();
        return res > 0;
    }

    //Устанавливает доступ к команде для группы
    public boolean createAccess(int idGroup, String command, boolean privilege) throws SQLException {
        int idCommand;
        try {
            idCommand = CommandDBController.getInstance().getId(command);
        } catch (NotFoundException e) {
            LOG.debug("Create new command: " + command);
            idCommand = CommandDBController.getInstance().createCommand(command);
        }

        return createAccess(idGroup, idCommand, privilege);
    }

    //Проверяет доступна ли комманда пользователю
    public boolean hasAccessUser(int idUser, int idCommand) throws SQLException {
        int idGroup = UserDBController.getInstance().getGroup(idUser);
        if (idGroup == -1) {
            return false;
        }

        return hasAccessGroup(idGroup, idCommand);
    }

    //Проверяет доступна ли комманда группе
    public boolean hasAccessGroup(int idGroup, int idCommand) throws SQLException {
        //Ищем имя команды
        String commandName = CommandDBController.getInstance().getName(idCommand);
        if (commandName == null) {
            return false;
        }

        return hasAccessGroup(idGroup, commandName);
    }

    public boolean hasAccessGroup(int idGroup, String commandName) throws SQLException {
        String sql = "SELECT access, id_command, commands.name " +
                "FROM groups_privilege LEFT JOIN commands ON id_command = commands.id " +
                "WHERE id_group = ?";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        ResultSet res = prStatement.executeQuery();

        //Ищем доступ
        boolean access = false;
        boolean dbAccess;
        String dbCommandName;
        while (res.next()) {
            dbAccess = res.getBoolean("access");
            dbCommandName = res.getString("name");
            if (!dbAccess && commandName.indexOf(dbCommandName) == 0) {
                access = false;
                break;
            } else if (!access && dbAccess && commandName.indexOf(dbCommandName) == 0) {
                access = true;
            }
        }

        res.close();
        return access;
    }

    //Изменяет доступ к команде для группы
    public boolean editAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        String sql = "UPDATE groups_privilege " +
                "SET access = ? " +
                "WHERE id_group = ? AND id_command = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setBoolean(1, privilege);
        prStatement.setInt(2, idGroup);
        prStatement.setInt(3, idCommand);
        int res = prStatement.executeUpdate();

        prStatement.close();
        return res > 0;
    }

    //Удаляет из БД запись с доступом к команде
    public boolean deleteAccess(int idGroup, int idCommand) throws SQLException {
        String sql = "DELETE FROM groups_privilege " +
                "WHERE id_group = ? AND id_command = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setInt(2, idCommand);
        int res = prStatement.executeUpdate();

        prStatement.close();
        return res > 0;
    }
}
