package dikanev.nikita.core.api.item;

import java.util.HashSet;
import java.util.Set;

public class Photo {
    public int id;

    public int userCreatorId;

    public int ownerId;

    public String link;

    public String description = null;

    public String[] shown;

    public Photo(int userId, String link, String... shown) {
        this.userCreatorId = userId;
        this.ownerId = userId;
        this.link = link;
        this.shown = shown;
    }

    public Photo(int id, int userCreatorId, int userOwnerId, String link, String[] shown) {
        this.id = id;
        this.userCreatorId = userCreatorId;
        this.ownerId = userOwnerId;
        this.link = link;
        this.shown = shown;
    }

    public static enum Shown {
        USER("user"),
        AMMUNITION("ammunition"),
        TEAM("team"),
        OTHER("other");

        public String name;

        Shown(String name) {
            this.name = name;
        }
    }
}
