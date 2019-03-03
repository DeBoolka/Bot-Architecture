package dikanev.nikita.core.service.server;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dikanev.nikita.core.service.item.parameter.HttpGetParameter;
import dikanev.nikita.core.service.item.parameter.Parameter;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class CommandParser {

    public final static String ENTER_METHOD = "O-Enter-Method";
    public final static String PATH_SELF = "O-PATH-SELF";
    public final static String PATH_ABSOLUTE = "O-PATH-ABSOLUTE";

    private List<String> commandPath;

    private JsonParser jsParser = new JsonParser();

    public Parameter headers = new HttpGetParameter();

    public CommandParser(String command) {
        this.commandPath = Arrays.asList(command.split("/"));

        parseEnterMethod();
        this.headers.set(PATH_ABSOLUTE, command);
    }

    public List<String> getCommandPath() {
        return commandPath;
    }

    private void parseEnterMethod() {
        if (commandPath.isEmpty()) {
            return;
        }

        String lastElement = commandPath.get(commandPath.size() - 1);
        String[] elementAndEnterMethod = lastElement.split("\\.");

        if (elementAndEnterMethod.length > 1) {
            headers.set("O-Enter-Method", elementAndEnterMethod[1]);
        }
    }

    public void parseJsonHeaders(JsonObject headers) {
        headers.entrySet().forEach((entry) -> {
            String key = entry.getKey();
            String val = entry.getValue().getAsString();

            if (!this.headers.contains(key)) {
                this.headers.set(key, val);
            }
        });
    }

    public void parseJsonHeaders(String json) {
        parseJsonHeaders(jsParser.parse(json).getAsJsonObject());
    }

    public void parseHttpHeaders(HttpServletRequest req) {
        req.getHeaderNames().asIterator().forEachRemaining(it -> {
            if (!headers.contains(it)) {
                this.headers.set(it, req.getHeader(it));
            }
        });
    }

    public String getRoute() {
        return Joiner.on("/").join(commandPath);
    }

    public void addHeaders(Parameter headers) {
        headers.keySet().forEach(key -> this.headers.add(key, headers.get(key)));
    }
}
