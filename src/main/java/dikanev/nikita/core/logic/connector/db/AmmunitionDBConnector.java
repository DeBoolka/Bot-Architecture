package dikanev.nikita.core.logic.connector.db;

import dikanev.nikita.core.service.server.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AmmunitionDBConnector {

    public static int addAmmunition(int userId, String ammoName) throws SQLException {
        if (ammoName == null || userId < 0) {
            throw new IllegalStateException("Invalid is ammoName or userId");
        }

        String sql = "SELECT ADD_AMMUNITION(?, ?) AS id";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql)
                .set(p -> p.setInt(1, userId))
                .set(p -> p.setString(2, ammoName));

        ResultSet res = req.executeQuery();
        int id = -1;
        if (res.next()) {
            id = res.getInt("id");
        }

        res.close();
        req.close();
        return id;
    }
}
