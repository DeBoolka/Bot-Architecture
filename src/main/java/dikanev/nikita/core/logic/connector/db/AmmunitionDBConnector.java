package dikanev.nikita.core.logic.connector.db;

import com.google.common.base.Joiner;
import dikanev.nikita.core.api.item.Ammo;
import dikanev.nikita.core.api.item.Photo;
import dikanev.nikita.core.service.server.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String sql = "SELECT user_ammunition.id AS ammunitionId, id_user, name, img.id AS imgId, link " +
                "FROM (SELECT * FROM user_ammunitions WHERE id_user = ? AND NOT is_delete ORDER BY id LIMIT ?, ?) AS user_ammunition " +
                "   LEFT JOIN img ON user_ammunition.id = id_owner " +
                "WHERE (img.id IS NULL OR shown = 'ammunition') " +
                "ORDER BY ammunitionId";

        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql)
                .set(p -> {
                    p.setInt(1, userId);
                    p.setInt(2, indent);
                    p.setInt(3, count);
                });

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
            imgId = res.getInt("imgId");

            if (ammunition.isEmpty() || ammunitionId != ammunition.get(ammunition.size() - 1).id) {
                photos = new ArrayList<>();
                ammunition.add(new Ammo(ammunitionId, userId, name, photos));
            }

            if (!res.wasNull()) {
                link = res.getString("link");
                photos.add(new Photo(imgId, userId, ammunitionId, link, Photo.Shown.AMMUNITION.name));
            }
        }

        res.close();
        req.close();
        return ammunition;
    }

    public static boolean deleteAmmunition(Integer[] ammunitionId) throws SQLException {
        if (ammunitionId == null || ammunitionId.length == 0) {
            return true;
        }

        String sql = "UPDATE user_ammunitions SET is_delete = true WHERE id IN (" + Joiner.on("' , '").join(ammunitionId) + ") AND NOT is_delete";
        Statement statement = DBStorage.getInstance().getConnection().createStatement();
        int res = statement.executeUpdate(sql);

        statement.close();
        return res > 0;
    }
}
