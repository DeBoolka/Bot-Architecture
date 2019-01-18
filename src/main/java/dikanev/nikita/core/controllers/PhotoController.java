package dikanev.nikita.core.controllers;

import dikanev.nikita.core.api.item.Photo;
import dikanev.nikita.core.logic.connector.db.PhotoDBConnector;

import java.sql.SQLException;
import java.util.List;

public class PhotoController {
    public static List<Photo> addPhoto(int userId, int ammunitionId, String[] links, String... shown) throws SQLException {
        return PhotoDBConnector.addPhoto(userId, ammunitionId, links, shown);
    }
}
