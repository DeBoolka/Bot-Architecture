package dikanev.nikita.core.mockers.db;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.logic.connector.db.users.UserDBConnector;
import org.mockito.InjectMocks;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class UserDBMock {
    private static UserDBMock ourInstance;

    @InjectMocks
    public UserDBConnector userDBMock = mock(UserDBConnector.class);

    public static UserDBMock getInstance() {
        if (ourInstance == null) {
            ourInstance = new UserDBMock();
        }
        return ourInstance;
    }

    private UserDBMock()  {
        try {
            init();
            setOurInstanceToBaseClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws SQLException, NotFoundException {
        when(userDBMock.createUser(anyString(), anyString(), anyInt())).thenReturn(1, 2, 3);
        when(userDBMock.createUser(anyInt(), anyString(), anyString(), anyInt())).thenReturn(1, 2, 3);

        when(userDBMock.deleteUser(anyInt())).thenReturn(true);

        when(userDBMock.toGroup(anyInt(), anyInt())).thenReturn(true);

        when(userDBMock.getData(anyInt()))
                .thenReturn(new HashMap(Map.of("id_group", 2, "s_name", "", "name", "")));
        when(userDBMock.getData(1))
                .thenReturn(new HashMap(Map.of("id_group", 1, "s_name", "", "name", "")));

        when(userDBMock.getData(anyString())).thenReturn(new HashMap(Map.of("id_group", 1, "s_name", "", "name", "")));

        when(userDBMock.createToken(anyInt())).thenReturn(String.valueOf(System.nanoTime()));

        when(userDBMock.deleteToken(anyInt())).thenReturn(true);

        when(userDBMock.getGroup(anyInt())).thenReturn(3);
        when(userDBMock.getGroup(1)).thenReturn(1);
        when(userDBMock.getGroup(2)).thenReturn(2);
    }

    private void setOurInstanceToBaseClass() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = UserDBConnector.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(UserDBConnector.getInstance(), userDBMock);
    }
}
