package dikanev.nikita.core.controllers.groups;

import dikanev.nikita.core.Application;
import dikanev.nikita.core.service.storage.DBStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AccessGroupControllerTest {

    @BeforeAll
    static void init(){
        Properties properties = loadConfiguration();

        String urlDB = properties.getProperty("db.url");
        String loginDB = properties.getProperty("db.login");
        String passwordDB = properties.getProperty("db.password");
        DBStorage.getInstance().init(urlDB, loginDB, passwordDB);
    }

    @Test
    void hasAccessUser() {
        int idUser = 4;
        try {
            assertTrue(AccessGroupController.getInstance().hasAccessUser(idUser, 2));
            assertFalse(AccessGroupController.getInstance().hasAccessUser(idUser, 1));
            assertFalse(AccessGroupController.getInstance().hasAccessUser(idUser, 11));
            assertFalse(AccessGroupController.getInstance().hasAccessUser(idUser, 6));
        } catch (Exception e) {
            assertNotNull(null);
        }
    }

    @Test
    void hasAccessGroup() {
        int idGroup = 2;
        try {
            assertTrue(AccessGroupController.getInstance().hasAccessGroup(idGroup, 2));
            assertFalse(AccessGroupController.getInstance().hasAccessGroup(idGroup, 1));
            assertFalse(AccessGroupController.getInstance().hasAccessGroup(idGroup, 11));
            assertFalse(AccessGroupController.getInstance().hasAccessGroup(idGroup, 6));
        } catch (Exception e) {
            assertNotNull(null);
        }
    }

    @Test
    void hasAccessGroup1() {
        int idGroup = 2;
        try {
            assertTrue(AccessGroupController.getInstance().hasAccessGroup(idGroup, "user/delete"));
            assertFalse(AccessGroupController.getInstance().hasAccessGroup(idGroup, "user/register"));
            assertFalse(AccessGroupController.getInstance().hasAccessGroup(idGroup, "group/"));
            assertFalse(AccessGroupController.getInstance().hasAccessGroup(idGroup, "group/delete"));
        } catch (Exception e) {
            assertNotNull(null);
        }
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream is = Application.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            assertNotNull(null);
        }

        return properties;
    }
}