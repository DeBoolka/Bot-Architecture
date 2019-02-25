package dikanev.nikita.core.logic.commands.group.access;

import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.mockers.DBMock;

import dikanev.nikita.core.service.item.parameter.HttpGetParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateAccessGroupCommandTest {

    @BeforeEach
    void setUp() {
        DBMock.init();
    }

    @Test
    void work() {
        CreateAccessGroupCommand cac = new CreateAccessGroupCommand(1);
        MessageObject mes;
        try {
            mes = (MessageObject) cac.work(new User(1, true), new HttpGetParameter(Map.of(
                    "id_group", new String[]{"1"},
                    "id_command", new String[]{"1"},
                    "access", new String[]{"true"}
            )));
            assertEquals(mes.getMessage(), "Ok");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            mes = (MessageObject) cac.work(new User(1, true), new HttpGetParameter(Map.of(
                    "id_group", new String[]{"1"},
                    "name", new String[]{"One"},
                    "access", new String[]{"true"}
            )));
            assertEquals(mes.getMessage(), "Ok");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            mes = (MessageObject) cac.work(new User(1, true), new HttpGetParameter(Map.of(
                    "id_group", new String[]{"1"},
                    "id_command", new String[]{"1", "2"},
                    "access", new String[]{"true"}
            )));
            assertEquals(mes.getMessage(), "Ok");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}