package dikanev.nikita.core.api.objects;

import com.google.gson.JsonArray;

public class SimpleArrayObject extends ApiObject {
    JsonArray objects;

    public SimpleArrayObject(JsonArray array) {
        super("array");
        objects = array;
    }
}
