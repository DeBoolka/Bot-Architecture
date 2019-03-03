package dikanev.nikita.core.api.objects;

import com.google.gson.Gson;

public class ApiObject {

    public static final Gson gson = new Gson();

    private String type;

    public ApiObject(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public String getJson(){
        return gson.toJson(this, this.getClass());
    }
}
