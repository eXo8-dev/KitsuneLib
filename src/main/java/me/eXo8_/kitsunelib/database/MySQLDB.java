package me.eXo8_.kitsunelib.database;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class MySQLDB implements Database
{
    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public MySQLDB(String host, int port, String database, String user, String password)
    {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    public void connect()
    {
        try
        {
            if (connection != null && !connection.isClosed()) return;

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true",
                    user,
                    password
            );
        } catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void disconnect() {
        try {if (connection != null) connection.close();}
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public boolean isConnected()
    {
        try {return connection != null && !connection.isClosed();}
        catch (SQLException e) {return false;}
    }

    @Override
    public void executeUpdate(String sql, Object... params)
    {
        try (PreparedStatement st = connection.prepareStatement(sql))
        {
            for (int i = 0; i < params.length; i++) st.setObject(i + 1, params[i]);
            st.executeUpdate();
        } catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void executeQuery(String sql, QueryHandler handler, Object... params)
    {
        try (PreparedStatement st = connection.prepareStatement(sql))
        {
            for (int i = 0; i < params.length; i++) st.setObject(i + 1, params[i]);
            try (ResultSet rs = st.executeQuery()) {handler.handle(rs);}
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public CompletableFuture<Void> executeQueryAsync(String sql, QueryHandler handler, Object... params) {
        return CompletableFuture.runAsync(() -> executeQuery(sql, handler, params));
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
