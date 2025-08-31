package me.eXo8_.kitsunelib.database.dao;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AsyncDao<O, I> extends Dao<O, I>
{
    default CompletableFuture<Optional<O>> getAsync(I id) {
        return CompletableFuture.supplyAsync(() -> get(id));
    }
    default CompletableFuture<Collection<O>> getAllAsync() {
        return CompletableFuture.supplyAsync(this::all);
    }
    default CompletableFuture<Void> saveAsync(O object) {
        return CompletableFuture.runAsync(() -> save(object));
    }
    default CompletableFuture<Void> updateAsync(O object) {
        return CompletableFuture.runAsync(() -> update(object));
    }
    default CompletableFuture<Void> deleteAsync(O object) {
        return CompletableFuture.runAsync(() -> delete(object));
    }
    default CompletableFuture<Boolean> existsAsync(I id) {
        return CompletableFuture.supplyAsync(() -> exists(id));
    }
}