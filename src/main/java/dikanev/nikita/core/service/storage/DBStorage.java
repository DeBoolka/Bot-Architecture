package dikanev.nikita.core.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBStorage {

    private static final Logger LOG = LoggerFactory.getLogger(DBStorage.class);

    private static DBStorage ourInstance = new DBStorage();

    private Connection connection = null;

    private String url = null;

    private String login = null;

    private String password = null;

    private long tokenDeleteTimeMinutes = 60*24*31;

    public DBStorage() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("Not found class: com.mysql.cj.jdbc.Driver");
        }
    }

    public static DBStorage getInstance() {
        return ourInstance;
    }

    public void init(String url, String login, String password){
        this.url = url;
        this.login = login;
        this.password = password;

        connect();
    }

    //Подключение к БД
    private void connect() throws IllegalStateException{
        try {
            if (connection != null) {
                connection.close();
            }

            connection = DriverManager.getConnection(url, login, password);
            LOG.info("Connect DB");
        } catch (SQLException e) {
            connection = null;
            LOG.error("Can't connect to the DB");
            throw new IllegalStateException(e);
        }
    }

    public Connection getConnection() throws SQLException{
        if(connection.isValid(30)){
            return connection;
        }

        connect();

        return connection;
    }

    public void setTokenDeleteTime(long minutes) {
        tokenDeleteTimeMinutes = minutes;
    }

    public long getTokenDeleteTimeMinutes(){
        return tokenDeleteTimeMinutes;
    }
}
