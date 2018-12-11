package dikanev.nikita.core.logic.commands.check;

import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.mockers.DBMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckWorkCommandTest {

    @BeforeEach
    void setUp() {
        DBMock.init();
    }

    @Test
    void work() {
        CheckWorkCommand cw = new CheckWorkCommand(1);
        MessageObject res = null;
        try {
             res = (MessageObject) cw.work(null, null);
        } catch (Exception e) {
            fail();
        }

        assertEquals("Ok", res.getMessage());
    }
}