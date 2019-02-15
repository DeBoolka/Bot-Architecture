package dikanev.nikita.core.api.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.List;

public class SimpleArrayObject extends ApiObject {
    private static Gson gson = new Gson();
    JsonArray objects;

    public SimpleArrayObject(JsonArray array) {
        super("array");
        objects = array;
    }

    public <T> SimpleArrayObject(List<T> array) {
        super("array");
        this.objects = gson.toJsonTree(array, array.getClass()).getAsJsonArray();
    }
}
