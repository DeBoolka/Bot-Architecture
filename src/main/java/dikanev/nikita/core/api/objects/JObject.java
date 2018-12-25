package dikanev.nikita.core.api.objects;

import com.google.gson.JsonObject;

public class JObject extends ApiObject {
    private JsonObject object;

    public JObject(String type) {
        super(type);
    }

    public JObject(JsonObject js) {
        super(js.has("type") ? js.get("type").getAsString() : "unknown");
        js.remove("type");

        object = js;
    }
}
