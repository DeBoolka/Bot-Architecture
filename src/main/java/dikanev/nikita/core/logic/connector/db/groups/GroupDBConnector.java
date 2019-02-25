package dikanev.nikita.core.logic.connector.db.groups;

import dikanev.nikita.core.logic.connector.db.SQLHelper;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(GroupDBConnector.class);

    private static GroupDBConnector ourInstance = new GroupDBConnector();

    public static GroupDBConnector getInstance() {
        return ourInstance;
    }

    //Создание группы
    public int createGroup(String name) throws SQLException {
        String sql = "INSERT groups(id, name) VALUES (NULL, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setString(1, name);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            LOG.warn("Failed to create a group with the name: " + name);
            throw new IllegalStateException("Failed to create a group with the name: " + name);
        }

        return SQLHelper.getLastId();
    }

    //Создание группы
    public int createGroup(String name, int id) throws SQLException {
        String sql = "INSERT groups(id, name) VALUES (?, ?)";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, id);
        prStatement.setString(2, name);
        int res = prStatement.executeUpdate();
        prStatement.close();

        if (res == 0) {
            LOG.warn("Failed to create a group with the name: " + name);
            throw new IllegalStateException("Failed to create a group with the name: " + name);
        }

        return id;
    }

    //Удаление группы
    public boolean deleteGroup(int idGroup) throws SQLException {
        String sql = "DELETE FROM groups " +
                "WHERE id = ? " +
                "LIMIT 1";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        int countDelete = prStatement.executeUpdate();
        prStatement.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete group with id: " + idGroup);
            return false;
        }

        sql = "DELETE FROM groups_privilege " +
                "WHERE id_group = ? ";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.executeUpdate();
        prStatement.close();

        sql = "UPDATE users SET id_group = 3 " +
                "WHERE id_group = ?";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.executeUpdate();
        prStatement.close();

        return true;
    }

    //Получение имени группы
    public String getName(int idGroup) throws SQLException {
        String sql = "SELECT name " +
                "FROM groups " +
                "WHERE id = ? " +
                "LIMIT 1;";

        PreparedStatement prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        ResultSet res = prStatement.executeQuery();

        String nameGroup = null;
        while (res.next()) {
            nameGroup = res.getString("name");
        }

        res.close();
        return nameGroup;
    }
}
