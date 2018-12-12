package dikanev.nikita.core.api.users;

import java.util.Date;

public class UserInfo {
    private int userId;

    private String login;

    private String email;

    private Date age = null;

    private String phone = null;

    private String city = null;

    private String nameOnGame = null;

    public UserInfo(String login) {
        this.login = login;
    }

    public UserInfo(int userId) {
        this.userId = userId;
    }

    public UserInfo(String email, boolean emailFlag) {
        this.email = email;
    }

    public UserInfo() {
    }

    public int getUserId() {
        return userId;
    }

    public UserInfo setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public UserInfo setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInfo setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getAge() {
        return age;
    }

    public UserInfo setAge(Date age) {
        this.age = age;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserInfo setCity(String city) {
        this.city = city;
        return this;
    }

    public String getNameOnGame() {
        return nameOnGame;
    }

    public UserInfo setNameOnGame(String nameOnGame) {
        this.nameOnGame = nameOnGame;
        return this;
    }
}
