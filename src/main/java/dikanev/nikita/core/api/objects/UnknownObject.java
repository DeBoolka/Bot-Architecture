package dikanev.nikita.core.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UnknownObject extends ApiObject {
    JsonElement element;

    public UnknownObject(String type, JsonElement element) {
        super(type);
        this.element = element;
    }
}
