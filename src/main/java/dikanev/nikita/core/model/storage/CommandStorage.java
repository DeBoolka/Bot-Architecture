package dikanev.nikita.core.model.storage;

import dikanev.nikita.core.model.commands.Command;
import dikanev.nikita.core.model.commands.check.CheckWorkCommand;
import dikanev.nikita.core.model.commands.group.*;
import dikanev.nikita.core.model.commands.user.DeleteUserCommand;
import dikanev.nikita.core.model.commands.user.GetUserCommand;
import dikanev.nikita.core.model.commands.user.NewUserCommand;
import dikanev.nikita.core.model.commands.user.create.CreateTokenCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandStorage {
    private static CommandStorage instance = new CommandStorage();

    private final Map<String, Command> commands = new HashMap<>();

    public static CommandStorage getInstance() {
        return instance;
    }

    private CommandStorage() {
        initCommand();
    }

    //Инициализация команд
    private void initCommand() {
        Map<Integer, String> commandBD = Command.getCommands();

        addCommand(commandBD.get(1), new NewUserCommand(1));
        addCommand(commandBD.get(2), new DeleteUserCommand(2));
        addCommand(commandBD.get(3), new CreateTokenCommand(3));
        addCommand(commandBD.get(4), new GetUserCommand(4));
        addCommand(commandBD.get(5), new CreateGroupCommand(5));
        addCommand(commandBD.get(6), new DeleteGroupCommand(6));
        addCommand(commandBD.get(7), new GetGroupCommand(7));
        addCommand(commandBD.get(8), new CreateAccessGroupCommand(8));
        addCommand(commandBD.get(9), new DeleteAccessGroupCommand(9));
        addCommand(commandBD.get(10), new CheckWorkCommand(10));
    }

    public void addCommand(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String command){
        return commands.get(command);
    }
}
