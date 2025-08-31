package me.eXo8_.kitsunelib.database.dao;

import java.util.Collection;
import java.util.Optional;

public interface Dao<O, I>
{
    Optional<O> get(I id);
    Collection<O> all();
    void save(O object);
    void update(O object);
    void delete(O object);
    boolean exists(I id);
}
