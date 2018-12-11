package dikanev.nikita.core.service.storage;

import dikanev.nikita.core.logic.commands.user.RegisterUserCommand;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandStorageTest {

    final String PATH_TO_COMMANDS_ROUTE = "src\\test\\resources\\commands_route.json";

    @Test
    void init() throws SQLException {
        CommandStorage cs = CommandStorage.getInstance();
        cs.init(PATH_TO_COMMANDS_ROUTE, "IGNORE");
        System.out.println(RegisterUserCommand.class);
        System.out.println(cs.getCommand("/test").getClass().getName());
    }

    @Test
    void getCommands() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = CommandStorage.class.getDeclaredMethod("getCommands", String.class);
        method.setAccessible(true);
        Map<Integer, String[]> commands = (Map<Integer, String[]>) method.invoke(CommandStorage.getInstance(), PATH_TO_COMMANDS_ROUTE);
        assertEquals("/test", commands.get(1)[0]);
        assertEquals("dikanev.nikita.core.logic.commands.user.RegisterUserCommand", commands.get(1)[1]);
        assertEquals("Test Command", commands.get(1)[2]);
    }
}