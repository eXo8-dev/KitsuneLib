package me.eXo8_.kitsunelib.database.dao.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.eXo8_.kitsunelib.database.Database;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class CachedDao<O, I> extends AbstractDao<O, I>
{
    private final Cache<I, O> cache;

    public CachedDao(Database db, String table, long maxSize, long expireAfterWrite, TimeUnit unit)
    {
        super(db, table);
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, unit)
                .build();
    }

    @Override
    public Optional<O> get(I id)
    {
        O value = cache.get(id, this::loadFromDb);
        return Optional.ofNullable(value);
    }

    private O loadFromDb(I id)
    {
        final O[] result = (O[]) new Object[1];

        db.executeQuery("SELECT * FROM " + table + " WHERE id=?", rs -> {
            if (rs.next()) result[0] = mapRow(rs);
        }, id);

        return result[0];
    }

    @Override
    public Collection<O> all()
    {
        Collection<O> objects = super.all();
        objects.forEach(o -> cache.put(getId(o), o));
        return objects;
    }

    @Override
    public void save(O object)
    {
        saveData(object);
        cache.put(getId(object), object);
    }

    @Override

    public void update(O object) {
        updateData(object);
        cache.put(getId(object), object);
    }

    @Override
    public void delete(O object)
    {
        super.delete(object);
        cache.invalidate(getId(object));
    }

    @Override
    public boolean exists(I id)
    {
        if (cache.getIfPresent(id) != null) return true;
        return super.exists(id);
    }

    abstract protected void saveData(O object);
    abstract protected void updateData(O object);
}
