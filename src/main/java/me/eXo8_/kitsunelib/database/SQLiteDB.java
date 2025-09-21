package me.eXo8_.kitsunelib.database;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.database.dao.Dao;

import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteDB implements Database
{
    private Connection connection;
    private final File file;

    private final Map<Class<? extends Dao<?, ?>>, Dao<?, ?>> daoRegistry = new ConcurrentHashMap<>();

    public SQLiteDB(String databaseName) {
        this.file = new File(KitsuneLib.getPlugin().getDataFolder(), databaseName);
    }

    @Override
    public void connect()
    {
        try
        {
            if (connection != null && !connection.isClosed()) return;

            File folder = KitsuneLib.getPlugin().getDataFolder();
            if (!folder.exists()) folder.mkdirs();

            if (!file.exists()) file.createNewFile();

            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        }
        catch (Exception e) {e.printStackTrace();}
    }


    @Override
    public void disconnect()
    {
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
        }
        catch (SQLException e) {e.printStackTrace();}
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

    protected <T extends Dao<?, ?>> void registerDao(Class<T> clazz, T dao) {
        daoRegistry.put(clazz, dao);
    }

    public <T extends Dao<?, ?>> T getDao(Class<T> clazz) {
        return (T) daoRegistry.get(clazz);
    }
}
