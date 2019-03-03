package dikanev.nikita.core.mockers.db;

import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.logic.connector.db.groups.AccessGroupDBConnector;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AccessGroupDBMock {
    private static AccessGroupDBMock ourInstance;

    @Mock
    public AccessGroupDBConnector accessGroupDB = mock(AccessGroupDBConnector.class);

    public static AccessGroupDBMock getInstance() {
        if (ourInstance == null) {
            ourInstance = new AccessGroupDBMock();
        }
        return ourInstance;
    }

    private AccessGroupDBMock()  {
        try {
            init();
            setOurInstanceToBaseClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws SQLException, NotFoundException {
        when(accessGroupDB.createAccess(anyInt(), anyInt(), anyBoolean())).thenReturn(true);
        when(accessGroupDB.createAccess(anyInt(), any(int[].class), anyBoolean())).thenReturn(true);
        when(accessGroupDB.createAccess(anyInt(), anyString(), anyBoolean())).thenReturn(true);
        when(accessGroupDB.createAccess(anyInt(), any(String[].class), anyBoolean())).thenReturn(true);

        when(accessGroupDB.hasAccessUser(anyInt(), anyInt())).thenReturn(false);
        when(accessGroupDB.hasAccessUser(1, 1)).thenReturn(true);
        when(accessGroupDB.hasAccessUser(1, 2)).thenReturn(true);
        when(accessGroupDB.hasAccessUser(2, 1)).thenReturn(false);
        when(accessGroupDB.hasAccessUser(2, 2)).thenReturn(false);

        when(accessGroupDB.hasAccessGroup(anyInt(), anyInt())).thenReturn(false);
        when(accessGroupDB.hasAccessGroup(1, 1)).thenReturn(true);
        when(accessGroupDB.hasAccessGroup(1, 2)).thenReturn(true);
        when(accessGroupDB.hasAccessGroup(2, 1)).thenReturn(false);
        when(accessGroupDB.hasAccessGroup(2, 2)).thenReturn(false);

        when(accessGroupDB.hasAccessGroup(anyInt(), anyString())).thenReturn(false);
        when(accessGroupDB.hasAccessGroup(1, "1")).thenReturn(true);
        when(accessGroupDB.hasAccessGroup(1, "2")).thenReturn(true);
        when(accessGroupDB.hasAccessGroup(2, "1")).thenReturn(false);
        when(accessGroupDB.hasAccessGroup(2, "2")).thenReturn(false);

        when(accessGroupDB.getAccessGroup(anyInt(), any())).thenReturn(new HashMap(Map.of("1", false, "2", false)));
        when(accessGroupDB.getAccessGroup(eq(1), any())).thenReturn(new HashMap(Map.of("1", true, "2", true)));
        when(accessGroupDB.getAccessGroup(eq(2), any())).thenReturn(new HashMap(Map.of("1", false, "2", false)));

        when(accessGroupDB.editAccess(anyInt(), anyInt(), anyBoolean())).thenReturn(true);

        when(accessGroupDB.deleteAccess(anyInt(), anyInt())).thenReturn(true);
    }

    private void setOurInstanceToBaseClass() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = AccessGroupDBConnector.class;

        Field field = clazz.getDeclaredField("ourInstance");
        field.setAccessible(true);
        field.set(AccessGroupDBConnector.getInstance(), accessGroupDB);
    }
}
