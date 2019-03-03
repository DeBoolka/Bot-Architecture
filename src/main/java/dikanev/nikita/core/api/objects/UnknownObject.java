package dikanev.nikita.core.api.objects;

import com.google.gson.JsonElement;

public class UnknownObject extends ApiObject {
    JsonElement element;

    public UnknownObject(String type, JsonElement element) {
        super(type);
        this.element = element;
    }
}
