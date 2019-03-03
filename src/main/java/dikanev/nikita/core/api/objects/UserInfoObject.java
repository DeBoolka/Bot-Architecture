package dikanev.nikita.core.api.objects;

import java.util.Date;

public class UserInfoObject extends ApiObject {
    private int userId;

    private String login;

    private String email;

    private Date age = null;

    private String phone = null;

    private String city = null;

    private String nameOnGame = null;

    public UserInfoObject(int userId, String login, String email, Date age, String phone, String city, String nameOnGame) {
        super("userInfo");

        this.userId = userId;
        this.login = login;
        this.email = email;
        this.age = age;
        this.phone = phone;
        this.city = city;
        this.nameOnGame = nameOnGame;
    }
}
