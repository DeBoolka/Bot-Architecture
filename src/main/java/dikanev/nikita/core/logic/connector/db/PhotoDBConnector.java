package dikanev.nikita.core.logic.connector.db;

import com.google.common.base.Joiner;
import dikanev.nikita.core.api.item.Photo;
import dikanev.nikita.core.service.server.SQLRequest;
import dikanev.nikita.core.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoDBConnector {
    private static final Logger LOG = LoggerFactory.getLogger(PhotoDBConnector.class);

    public static List<Photo> addPhoto(int userCreatorId, int userOwnerId, String[] links, String[] shown) throws SQLException {
        String sql = "SELECT ADD_PHOTO(?, ?, ?, ?, ?) AS id";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql)
                .set(p -> p.setInt(1, userCreatorId))
                .set(p -> p.setInt(2, userOwnerId))
                .set(p -> p.setNull(4, Types.VARCHAR))
                .set(p -> p.setString(5, Joiner.on(",").join(shown)));

        List<Photo> photos = new ArrayList<>(links.length);
        Arrays.stream(links).forEach(it -> {
            try {
                req.set(p -> p.setString(3, it));
                ResultSet res = req.executeQuery();
                if (res.next()) {
                    photos.add(new Photo(res.getInt("id"), userCreatorId, userOwnerId, it, shown));
                }
                res.close();
            } catch (SQLException e) {
                LOG.error("Failed add photo", e);
            }
        });

        req.close();
        return photos;
    }
}
