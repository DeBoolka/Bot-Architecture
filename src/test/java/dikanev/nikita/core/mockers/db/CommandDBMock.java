package dikanev.nikita.core.mockers.db;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import static org.mockito.Mockito.*;

import dikanev.nikita.core.controllers.db.commands.CommandDBController;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandDBMock {
    private static CommandDBMock ourInstance;

    @Mock
    public CommandDBController commandDBController = mock(CommandDBController.class);

    public static CommandDBMock getInstance() {
        if (ourInstance == null) {
            ourInstance = new CommandDBMock();
        }
        return ourInstance;
    }

    private CommandDBMock()  {
        try {
            init();
            setOurInstanceToBaseClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws SQLException, NotFoundException {
        when(commandDBController.createCommand("First")).thenReturn(1);
        when(commandDBController.createCommand("Second")).thenReturn(2);
        when(commandDBController.createCommand("Third")).thenReturn(3);

        when(commandDBController.deleteCommand(anyInt())).thenReturn(true);

        when(commandDBController.getName(1)).thenReturn("Third");
        when(commandDBController.getName(2)).thenReturn("Second");
        when(commandDBController.getName(3)).thenReturn("First");

        when(commandDBController.getId("First")).thenReturn(1);
        when(commandDBController.getId("Second")).thenReturn(2);
        when(commandDBController.getId("Third")).thenReturn(3);

        when(commandDBController.getCommands())
                .thenReturn(new HashMap<>(Map.of(
                        1, "First",
                        2, "Second",
                        3, "Third")));

    }

    private void setOurInstanceToBaseClass() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = CommandDBController.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(CommandDBController.getInstance(), commandDBController);
    }
}
