package dikanev.nikita.core.controllers;

import dikanev.nikita.core.api.item.Ammo;
import dikanev.nikita.core.api.item.Photo;
import dikanev.nikita.core.logic.connector.db.AmmunitionDBConnector;

import java.sql.SQLException;
import java.util.List;

public class AmmunitionController {
    public static Ammo addAmmunition(int userId, String ammoName, String[] photo) throws SQLException {
        int ammunitionId = AmmunitionDBConnector.addAmmunition(userId, ammoName);
        List<Photo> photos = PhotoController.addPhoto(userId, photo, Photo.Shown.AMMUNITION.name);

        return new Ammo(ammunitionId, userId, ammoName, photos);
    }
}
