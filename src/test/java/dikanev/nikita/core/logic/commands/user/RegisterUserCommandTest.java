package dikanev.nikita.core.logic.commands.user;

import dikanev.nikita.core.api.objects.UserObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.mockers.DBMock;
import dikanev.nikita.core.service.item.parameter.HttpGetParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserCommandTest {

    @BeforeEach
    void setUp() {
        DBMock.init();
    }

    @Test
    void work() {
        RegisterUserCommand nuc = new RegisterUserCommand(1);

        UserObject uso;
        try {
            uso = (UserObject) nuc.work(new User(1, true), new HttpGetParameter(Map.of(
                    "id_group", new String[]{"1"},
                    "s_name", new String[]{"Nik"},
                    "name", new String[]{"Dik"},
                    "id", new String[]{"5"}
            )));
            assertEquals(uso.getJson(), "{\"id\":5,\"idGroup\":1,\"sName\":\"Nik\",\"name\":\"Dik\",\"type\":\"user\"}");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            uso = (UserObject) nuc.work(new User(1, true), new HttpGetParameter(Map.of(
                    "id_group", new String[]{"1"},
                    "s_name", new String[]{"Nik"},
                    "name", new String[]{"Dik"}
            )));
            assertEquals(uso.getJson(), "Ok");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}