package dikanev.nikita.core.logic.connector.db.commands;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.logic.connector.db.SQLHelper;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CommandDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(CommandDBConnector.class);

    private static CommandDBConnector ourInstance = new CommandDBConnector();

    public static CommandDBConnector getInstance() {
        return ourInstance;
    }

    //Создание команды
    public int createCommand(String name) throws SQLException {
        String sql = "INSERT commands(id, name) VALUES (NULL, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, name);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            LOG.warn("Failed to create a command with the name: " + name);
            throw new IllegalStateException("Failed to create a command with the name: " + name);
        }

        return SQLHelper.getLastId();
    }

    //Удаление команды
    public boolean deleteCommand(int idCommand) throws SQLException {
        String sql = "DELETE FROM commands " +
                "WHERE id = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idCommand);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete command with id: " + idCommand);
            return false;
        }

        sql = "DELETE FROM groups_privilege " +
                "WHERE id_command = ? ";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idCommand);
        prStatement.executeUpdate();
        prStatement.close();

        return true;
    }

    //Получение имени комманды. //Получение имени комманды. Может вернуть null, если записи нет в бд
    public String getName(int idCommand) throws SQLException {
        String sql = "SELECT name " +
                "FROM commands " +
                "WHERE id = ? " +
                "LIMIT 1;";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idCommand);
        ResultSet res = prStatement.executeQuery();

        String nameCommand = null;
        while (res.next()) {
            nameCommand = res.getString("name");
        }

        res.close();
        return nameCommand;
    }

    //Получение id команды.
    public int getId(String name) throws SQLException, NotFoundException {
        String sql = "SELECT id " +
                "FROM commands " +
                "WHERE name = ? " +
                "LIMIT 1;";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, name);
        ResultSet res = prStatement.executeQuery();

        int id = -1;
        while (res.next()) {
            id = res.getInt("id");
        }

        res.close();
        if (id < 0) {
            throw new NotFoundException("Not found command");
        }

        return id;
    }

    //Получение всех имен комманд
    public Map<Integer, String> getCommands() throws SQLException {
        String sql = "SELECT * " +
                "FROM commands ";

        Statement statement = DBStorage.getInstance().getConnection().createStatement();
        ResultSet res = statement.executeQuery(sql);

        Map<Integer, String> commands = new HashMap<>();
        while (res.next()) {
            commands.put(res.getInt("id"), res.getString("name"));
        }

        res.close();
        return commands;
    }
}
