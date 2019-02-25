package dikanev.nikita.core.api.objects;

public class AccessGroupObject extends ApiObject {

    private int idGroup;

    private String command;

    private boolean access;

    public AccessGroupObject() {
        super("accessGroup");
    }

    public AccessGroupObject(int idGroup, String command, boolean access) {
        super("accessGroup");

        this.idGroup = idGroup;
        this.command = command;
        this.access = access;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getIdCommand() {
        return command;
    }

    public boolean isAccess() {
        return access;
    }

}
