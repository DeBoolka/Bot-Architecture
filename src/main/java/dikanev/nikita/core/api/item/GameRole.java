package dikanev.nikita.core.api.item;

public class GameRole {
    public int id;
    public String name;
    public String description = null;

    public GameRole() {
    }

    public GameRole(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
