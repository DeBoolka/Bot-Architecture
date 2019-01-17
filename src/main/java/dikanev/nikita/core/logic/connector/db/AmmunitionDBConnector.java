package dikanev.nikita.core.logic.connector.db;

import dikanev.nikita.core.api.item.Ammo;
import dikanev.nikita.core.api.item.Photo;
import dikanev.nikita.core.service.server.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Ammo> getAmmunition(int userId, int indent, int count) throws SQLException {
        String sql = "SELECT user_ammunitions.id AS ammunitionId, name, img.id AS imgId, link " +
                "FROM user_ammunitions " +
                "   LEFT JOIN img ON user_ammunitions.id = id_owner " +
                "   JOIN (SELECT id FROM user_ammunitions WHERE id_user = ? ORDER BY id LIMIT ?, ?) AS t ON user_ammunitions.id = t.id " +
                "WHERE id_user = ? AND (shown IS NULL OR shown = 'ammunition') " +
                "ORDER BY ammunitionId";

        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql)
                .set(p -> p.setInt(1, userId))
                .set(p -> p.setInt(2, indent))
                .set(p -> p.setInt(3, count))
                .set(p -> p.setInt(4, userId));

        ResultSet res = req.executeQuery();
        int ammunitionId;
        String name;
        int imgId;
        String link;
        List<Photo> photos = null;
        List<Ammo> ammunition = new ArrayList<>();
        while (res.next()) {
            ammunitionId = res.getInt("ammunitionId");
            name = res.getString("name");

            if (ammunition.isEmpty() || ammunitionId != ammunition.get(ammunition.size() - 1).id) {
                photos = new ArrayList<>();
                ammunition.add(new Ammo(ammunitionId, userId, name, photos));
            }

            imgId = res.getInt("imgId");
            if (!res.wasNull()) {
                link = res.getString("link");
                photos.add(new Photo(imgId, userId, ammunitionId, link, Photo.Shown.AMMUNITION.name));
            }
        }

        res.close();
        req.close();
        return ammunition;
    }
}
