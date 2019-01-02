package dikanev.nikita.core.service.storage;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dikanev.nikita.core.controllers.commands.CommandController;
import dikanev.nikita.core.logic.commands.Command;

import dikanev.nikita.core.service.server.CommandParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CommandStorage {
    private static final Logger LOG = LoggerFactory.getLogger(CommandStorage.class);

    private static CommandStorage instance = new CommandStorage();

    private final Map<String, Command> commands = new HashMap<>();
    private final Map<Integer, String> cacheNameCommand = new HashMap<>();

    private String routeConflict = "IGNORE";

    private final int ROUTE = 0;
    private final int CLASS_NAME = 1;
    private final int COMMAND_NAME = 2;
    private final int HEADERS = 3;

    public static CommandStorage getInstance() {
        return instance;
    }

    private CommandStorage() {
    }

    //Инициализация команд
    public void init(String pathToCommandsRoute, String routeConflict) {
        getCommands(pathToCommandsRoute).forEach((key, val) ->{
            try {
                CommandParser cmdParser = new CommandParser(val[ROUTE]);
                if (val[HEADERS] != null) {
                    cmdParser.parseJsonHeaders(val[HEADERS]);
                }

                Command instCommand = (Command) Class.forName(val[CLASS_NAME]).getDeclaredConstructor(int.class).newInstance(key);
                instCommand.setCommandParser(cmdParser);
                addCommand(val[ROUTE], instCommand);
            } catch (Exception e) {
                LOG.error("Init command error: ", e);
            }
        });

        this.routeConflict = routeConflict;
        conflictRoute();

    }

    private void conflictRoute() {
        switch (routeConflict) {
            case "IGNORE":
                return;
            case "PULL_AND_PUSH":
                pullAndPushCommand();
        }
    }

    private void pullAndPushCommand() {
        Map<Integer, String> dbCommand = CommandController.getCommands();
        if (dbCommand == null || dbCommand.equals(cacheNameCommand)) {
            return;
        }

        Map<String, Command> notContainedInDB = new HashMap<>();
        cacheNameCommand.clear();
        commands.forEach((k, v) -> {
            int keyFromDB = getKeyFromMap(k, dbCommand);
            if (keyFromDB == -1) {
                notContainedInDB.put(k, v);
            } else {
                v.setId(keyFromDB);
                cacheNameCommand.put(keyFromDB, k);
            }
            LOG.debug("Load command: " + k);
        });

        notContainedInDB.forEach((k, v) -> {
            int id = 1;
            try {
                id = CommandController.getInstance().createCommand(k);
            } catch (Exception e) {
                LOG.warn("Failed to write command " + k + " to database. Error: ", e);
                if (!cacheNameCommand.isEmpty()) {
                    id = cacheNameCommand.entrySet().stream()
                            .max(Comparator.comparing(Map.Entry::getKey))
                            .get().getKey() + 1;
                }
            }

            v.setId(id);
            cacheNameCommand.put(id, k);
        });
    }

    private int getKeyFromMap(String val, Map<Integer, String> map) {
        for (Map.Entry<Integer, String> temp : map.entrySet()) {
            if (temp.getValue().equals(val)) {
                return temp.getKey();
            }
        }

        return -1;
    }

    private Map<Integer, String[]> getCommands(String pathToCommandsRoute) {
        JsonArray commandsJson;
        final Gson gson = new Gson();
        final Map<Integer, String[]> commands = new HashMap<>();

        try (FileReader scanner = new FileReader(pathToCommandsRoute)) {
            commandsJson = gson.fromJson(scanner, JsonArray.class);

            commandsJson.iterator().forEachRemaining(command -> {
                JsonObject jo = command.getAsJsonObject();
                JsonElement jsHeaders;
                String headers = null;
                if ((jsHeaders = jo.get("headers")) != null) {
                    headers = jsHeaders.toString();
                }

                commands.put(jo.get("id").getAsInt()
                        , new String[]{jo.get("route").getAsString()
                                     , jo.get("class").getAsString()
                                     , jo.get("name").getAsString()
                                     , headers});
            });
        } catch (IOException e) {
            LOG.error("Commands route file not found. ", e);
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
        cacheNameCommand.put(command.getId(), name);
    }

    public Command getCommand(String command){
        return commands.get(command);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public int getIdCommand(String command) {
        Command commandObj = commands.get(command);
        if (commandObj == null) {
            return -1;
        }
        return commandObj.getId();
    }

    public String getNameCommand(int id) {
        return cacheNameCommand.get(id);
    }
}
