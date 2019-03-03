package dikanev.nikita.core.api.objects;

import com.google.gson.JsonArray;

import java.util.List;

public class SimpleArrayObject extends ApiObject {
    JsonArray objects;

    public SimpleArrayObject(JsonArray array) {
        super("array");
        objects = array;
    }

    public <T> SimpleArrayObject(List<T> array) {
        super("array");
        this.objects = gson.toJsonTree(array, array.getClass()).getAsJsonArray();
    }

    public <T> SimpleArrayObject(T[] array) {
        super("array");
        this.objects = gson.toJsonTree(array, array.getClass()).getAsJsonArray();
    }
}
