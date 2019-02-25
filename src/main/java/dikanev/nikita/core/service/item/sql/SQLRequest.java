package dikanev.nikita.core.service.item.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLRequest {

    private Connection connection;

    private PreparedStatement prStatement;

    public SQLRequest(Connection connection) {
        this.connection = connection;
    }

    public SQLRequest build(String sql) throws SQLException {
        prStatement = connection.prepareStatement(sql);
        return this;
    }

    public SQLRequest set(PrSt... statements) throws SQLException {
        for (PrSt temp : statements) {
            temp.set(prStatement);
        }
        return this;
    }

    public SQLRequest setInt(int parameterIndex, int value) throws SQLException {
        prStatement.setInt(parameterIndex, value);
        return this;
    }

    public SQLRequest setString(int parameterIndex, String value) throws SQLException {
        prStatement.setString(parameterIndex, value);
        return this;
    }

    public int executeUpdate() throws SQLException {
        return prStatement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException {
        return prStatement.executeQuery();
    }

    public SQLRequest close() throws SQLException {
        if (!prStatement.isClosed()) {
            prStatement.close();
        }
        return this;
    }

    public interface PrSt{
        void set(PreparedStatement preparedStatement) throws SQLException;
    }
}
