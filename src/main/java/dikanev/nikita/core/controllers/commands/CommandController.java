package dikanev.nikita.core.controllers.commands;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.controllers.db.commands.CommandDBController;
import dikanev.nikita.core.service.storage.CommandStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    private static CommandController ourInstance = new CommandController();

    public static CommandController getInstance() {
        return ourInstance;
    }

    //Создание команды
    public int createCommand(String name) throws SQLException {
        return CommandDBController.getInstance().createCommand(name);
    }

    //Удаление команды
    public boolean deleteCommand(int idCommand) throws SQLException {
        return CommandDBController.getInstance().deleteCommand(idCommand);
    }

    //Получение имени комманды. Может вернуть null, если записи нет в бд
    public String getName(int idCommand) {
        String commandName = CommandStorage.getInstance().getNameCommand(idCommand);
        if (commandName != null) {
            return commandName;
        }

        try {
            return CommandDBController.getInstance().getName(idCommand);
        } catch (SQLException e) {
            LOG.error("SQL exception in getName: ", e);
            return "";
        }
    }

    //Получение id комманды	    //Получение id комманды
    public int getId(String name) throws SQLException, NotFoundException {
        int id = CommandStorage.getInstance().getIdCommand(name);
        if (id == -1) {
            return CommandDBController.getInstance().getId(name);
        }
        return id;
    }

    //Получение всех имен комманд
    public static Map<Integer, String> getCommands() {
        try {
            return CommandDBController.getInstance().getCommands();
        } catch (SQLException e) {
            LOG.warn("Error in getCommands: ", e);

            final Map<Integer, String> res = new HashMap<>();
            CommandStorage.getInstance().getCommands().forEach((k, v) -> res.put(v.getId(), k));

            return res;
        }
    }

}
