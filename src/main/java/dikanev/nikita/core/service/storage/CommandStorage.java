package dikanev.nikita.core.service.storage;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dikanev.nikita.core.logic.commands.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandStorage {
    private static final Logger LOG = LoggerFactory.getLogger(CommandStorage.class);

    private static CommandStorage instance = new CommandStorage();

    private final Map<String, Command> commands = new HashMap<>();

    public static CommandStorage getInstance() {
        return instance;
    }

    private CommandStorage() {
    }

    //Инициализация команд
    public void init(String pathToCommandsRoute) {
        getCommands(pathToCommandsRoute).forEach((key, val) ->{
            try {
                addCommand(val[0], (Command) Class.forName(val[1]).getDeclaredConstructor(int.class).newInstance(key));
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getMessage());
            }
        });
    }

    private Map<Integer, String[]> getCommands(String pathToCommandsRoute) {
        JsonArray commandsJson;
        final Gson gson = new Gson();
        final Map<Integer, String[]> commands = new HashMap<>();

        try (FileReader scanner = new FileReader(pathToCommandsRoute)) {
            commandsJson = gson.fromJson(scanner, JsonArray.class);

            commandsJson.iterator().forEachRemaining(command -> {
                JsonObject jo = command.getAsJsonObject();
                commands.put(jo.get("id").getAsInt()
                        , new String[]{jo.get("route").getAsString()
                                     , jo.get("class").getAsString()
                                     , jo.get("name").getAsString()});
            });
        } catch (IOException e) {
            LOG.error("Commands route file not found.");
            return new HashMap<>();
        }

        commands.forEach((k, v) -> {
            if (k == null || v[0] == null || v[1] == null) {
                throw new IllegalStateException("Commands route file is corrupt");
            }
        });

        return commands;
    }

    public void addCommand(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String command){
        return commands.get(command);
    }

    public int getIdCommand(String command) {
        Command commandObj = commands.get(command);
        if (commandObj == null) {
            return -1;
        }
        return commandObj.getId();
    }

    public String getNameCommand(int id) {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            if (entry.getValue().getId() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}
