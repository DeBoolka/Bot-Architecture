package dikanev.nikita.core.controllers.commands;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.controllers.db.commands.CommandDBController;
import dikanev.nikita.core.service.storage.CommandStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class CommandController {

    private static final Logger LOG = LoggerFactory.getLogger(CommandController.class);

    private static CommandController ourInstance = new CommandController();

    private PreparedStatement prStatement;

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
    public String getName(int idCommand) throws SQLException {
        String commandName = CommandStorage.getInstance().getNameCommand(idCommand);
        if (commandName != null) {
            return commandName;
        }
        return CommandDBController.getInstance().getName(idCommand);
    }

    //Получение id комманды
    public int getId(String name) throws SQLException, NotFoundException {
        int id = CommandStorage.getInstance().getIdCommand(name);
        if (id == -1) {
            return CommandDBController.getInstance().getId(name);
        }
        return id;
    }

    //Получение всех имен комманд
    public Map<Integer, String> getCommands() throws SQLException {
        return CommandDBController.getInstance().getCommands();
    }
}
