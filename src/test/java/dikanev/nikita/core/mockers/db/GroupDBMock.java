package dikanev.nikita.core.mockers.db;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.logic.connector.db.groups.GroupDBConnector;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class GroupDBMock {
    private static GroupDBMock ourInstance;

    @Mock
    public GroupDBConnector groupDBConnector = mock(GroupDBConnector.class);

    public static GroupDBMock getInstance() {
        if (ourInstance == null) {
            ourInstance = new GroupDBMock();
        }
        return ourInstance;
    }

    private GroupDBMock()  {
        try {
            init();
            setOurInstanceToBaseClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws SQLException, NotFoundException {
        when(groupDBConnector.createGroup(anyString())).thenReturn(3);
        when(groupDBConnector.createGroup("First")).thenReturn(1);
        when(groupDBConnector.createGroup("Second")).thenReturn(2);

        when(groupDBConnector.createGroup(anyString(), anyInt())).thenReturn(3);

        when(groupDBConnector.deleteGroup(anyInt())).thenReturn(true);

        when(groupDBConnector.getName(anyInt())).thenReturn("Any");
        when(groupDBConnector.getName(1)).thenReturn("First");
        when(groupDBConnector.getName(2)).thenReturn("Second");
    }

    private void setOurInstanceToBaseClass() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = GroupDBConnector.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(GroupDBConnector.getInstance(), groupDBConnector);
    }
}
