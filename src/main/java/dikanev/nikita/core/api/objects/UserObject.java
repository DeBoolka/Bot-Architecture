package dikanev.nikita.core.api.objects;

public class UserObject extends ApiObject {

    private int id;

    private int idGroup;

    private String sName;

    private String name;

    public UserObject(int id, int idGroup, String sName, String name) {
        super("user");

        this.id = id;
        this.idGroup = idGroup;
        this.sName = sName;
        this.name = name;
    }

}
