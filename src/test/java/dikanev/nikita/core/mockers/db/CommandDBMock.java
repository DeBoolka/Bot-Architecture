package dikanev.nikita.core.mockers.db;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import static org.mockito.Mockito.*;

import dikanev.nikita.core.logic.connector.db.commands.CommandDBConnector;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandDBMock {
    private static CommandDBMock ourInstance;

    @Mock
    public CommandDBConnector commandDBConnector = mock(CommandDBConnector.class);

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
        when(commandDBConnector.createCommand("First")).thenReturn(1);
        when(commandDBConnector.createCommand("Second")).thenReturn(2);
        when(commandDBConnector.createCommand("Third")).thenReturn(3);

        when(commandDBConnector.deleteCommand(anyInt())).thenReturn(true);

        when(commandDBConnector.getName(1)).thenReturn("Third");
        when(commandDBConnector.getName(2)).thenReturn("Second");
        when(commandDBConnector.getName(3)).thenReturn("First");

        when(commandDBConnector.getId("First")).thenReturn(1);
        when(commandDBConnector.getId("Second")).thenReturn(2);
        when(commandDBConnector.getId("Third")).thenReturn(3);

        when(commandDBConnector.getCommands())
                .thenReturn(new HashMap<>(Map.of(
                        1, "First",
                        2, "Second",
                        3, "Third")));

    }

    private void setOurInstanceToBaseClass() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = CommandDBConnector.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(CommandDBConnector.getInstance(), commandDBConnector);
    }
}
