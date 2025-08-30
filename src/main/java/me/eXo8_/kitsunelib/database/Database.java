package me.eXo8_.kitsunelib.database;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface Database
{
    void connect();
    void disconnect();
    boolean isConnected();

    void executeUpdate(String sql, Object... params);
    void executeQuery(String sql, QueryHandler handler, Object... params);

    CompletableFuture<Void> executeQueryAsync(String sql, QueryHandler handler, Object... params);

    @FunctionalInterface
    interface QueryHandler {
        void handle(ResultSet rs) throws Exception;
    }
}