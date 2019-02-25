package dikanev.nikita.core.api.objects;

public class SimpleObject extends JObject {
    public final static String GAME = "game";

    public SimpleObject(String game, Object object) {
        super(game, gson.toJsonTree(object));
    }
}
