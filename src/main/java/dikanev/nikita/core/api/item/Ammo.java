package dikanev.nikita.core.api.item;

import java.util.List;

public class Ammo {

    public int id;

    public int ownerId;

    public String name;

    public List<Photo> photos = null;

    public Ammo(int id) {
        this.id = id;
    }

    public Ammo(int ownerId, String ammoName, List<Photo> photos) {
    }

    public Ammo(int id, int ownerId, String name, List<Photo> photos) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.photos = photos;
    }
}
