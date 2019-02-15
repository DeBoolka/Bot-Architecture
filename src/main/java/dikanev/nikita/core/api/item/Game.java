package dikanev.nikita.core.api.item;

import java.sql.Timestamp;

public class Game {
    int id;
    String name;
    int organizerId;
    String city;
    Timestamp date;

    public Game(int id, String name, int organizerId, String city, Timestamp date) {
        this.id = id;
        this.name = name;
        this.organizerId = organizerId;
        this.city = city;
        this.date = date;
    }
}
