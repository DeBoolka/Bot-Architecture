package dikanev.nikita.core.api.item;

public class RoleForGame {
    public GameRole role = null;
    public int numberOfAvailableSeats;
    public int userMaxCount = 20;
    public int armoredMaxCount = 0;

    public RoleForGame() {
    }

    public RoleForGame(GameRole role, int numberOfAvailableSeats, int userMaxCount, int armoredMaxCout) {
        this.role = role;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
        this.userMaxCount = userMaxCount;
        this.armoredMaxCount = armoredMaxCout;
    }
}
