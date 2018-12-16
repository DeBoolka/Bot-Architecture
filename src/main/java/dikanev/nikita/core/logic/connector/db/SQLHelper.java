package dikanev.nikita.core.logic.connector.db;

import dikanev.nikita.core.service.storage.DBStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLHelper {
    public static int getLastId() throws SQLException {
        String sql = "SELECT LAST_INSERT_ID() AS id";
        int lastId = -1;
        Statement statement = DBStorage.getInstance().getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            lastId = resultSet.getInt("id");
        }

        statement.close();
        return lastId;
    }
}
