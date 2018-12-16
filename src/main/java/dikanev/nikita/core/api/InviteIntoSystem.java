package dikanev.nikita.core.api;

import java.sql.Timestamp;

public class InviteIntoSystem {
    int userCreator;

    int groupId;

    String invite;

    Timestamp time;

    public InviteIntoSystem(int userCreator, int groupId, String invite, Timestamp time) {
        this.userCreator = userCreator;
        this.groupId = groupId;
        this.invite = invite;
        this.time = time;
    }

    public int getUserCreator() {
        return userCreator;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getInvite() {
        return invite;
    }

    public Timestamp getTime() {
        return time;
    }
}
