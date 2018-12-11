package dikanev.nikita.core.mockers;

import dikanev.nikita.core.mockers.db.AccessGroupDBMock;
import dikanev.nikita.core.mockers.db.CommandDBMock;
import dikanev.nikita.core.mockers.db.GroupDBMock;
import dikanev.nikita.core.mockers.db.UserDBMock;

public class DBMock {
    public static void init() {
        AccessGroupDBMock.getInstance();
        CommandDBMock.getInstance();
        GroupDBMock.getInstance();
        UserDBMock.getInstance();
    }
}
