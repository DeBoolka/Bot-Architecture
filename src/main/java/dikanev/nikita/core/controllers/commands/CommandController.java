package dikanev.nikita.core.controllers.commands;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.service.storage.CommandStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    private static CommandController ourInstance = new CommandController();

    public static CommandController getInstance() {
        return ourInstance;
    }

    //Получение имени комманды. Может вернуть null, если записи нет в бд
    public String getName(int idCommand) {
        return CommandStorage.getInstance().getNameCommand(idCommand);
    }

    //Получение id комманды
    public int getId(String name) throws NotFoundException {
        return CommandStorage.getInstance().getIdCommand(name);
    }

    //Получение всех имен комманд
    public Map<Integer, String> getCommands() {
        final Map<Integer, String> res = new HashMap<>();
        CommandStorage.getInstance().getCommands().forEach((k, v) -> res.put(v.getId(), k));

        return res;
    }

}
