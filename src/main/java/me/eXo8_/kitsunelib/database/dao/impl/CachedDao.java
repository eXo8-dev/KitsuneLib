package me.eXo8_.kitsunelib.database.dao.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.eXo8_.kitsunelib.database.Database;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
    public Optional<O> get(I id) {
        return Optional.ofNullable(cache.get(id, key -> loadFromDatabase(key).orElse(null)));
    }

    protected Optional<O> loadFromDatabase(I id)
    {
        AtomicReference<O> result = new AtomicReference<>();
        String idColumn = getIdColumnName();

        this.db.executeQuery("SELECT * FROM " + this.table + " WHERE " + idColumn + "=?", (rs) -> {
            if (rs.next()) result.set(this.mapRow(rs));
        }, id);

        return Optional.ofNullable(result.get());
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
    public boolean exists(I id) {
        return cache.getIfPresent(id) != null || super.exists(id);
    }

    public void invalidateCache(I id) {
        cache.invalidate(id);
    }

    abstract protected void saveData(O object);
    abstract protected void updateData(O object);
    abstract protected String getIdColumnName();
}
