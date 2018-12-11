package dikanev.nikita.core.mockers.db;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.controllers.db.commands.CommandDBController;
import static org.mockito.Mockito.*;

import dikanev.nikita.core.controllers.db.groups.GroupDBController;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GroupDBMock {
    private static GroupDBMock ourInstance;

    @Mock
    public GroupDBController groupDBController = mock(GroupDBController.class);

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
        when(groupDBController.createGroup(anyString())).thenReturn(3);
        when(groupDBController.createGroup("First")).thenReturn(1);
        when(groupDBController.createGroup("Second")).thenReturn(2);

        when(groupDBController.createGroup(anyString(), anyInt())).thenReturn(3);

        when(groupDBController.deleteGroup(anyInt())).thenReturn(true);

        when(groupDBController.getName(anyInt())).thenReturn("Any");
        when(groupDBController.getName(1)).thenReturn("First");
        when(groupDBController.getName(2)).thenReturn("Second");
    }

    private void setOurInstanceToBaseClass() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = GroupDBController.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(GroupDBController.getInstance(), groupDBController);
    }
}
