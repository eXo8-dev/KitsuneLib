package me.eXo8_.kitsunelib.database.dao.impl;

import me.eXo8_.kitsunelib.database.Database;
import me.eXo8_.kitsunelib.database.dao.Dao;

import java.sql.ResultSet;
import java.util.*;

public abstract class AbstractDao<O, I> implements Dao<O, I>
{
    protected final Database db;
    protected final String table;

    public AbstractDao(Database db, String table)
    {
        this.db = db;
        this.table = table;
    }

    protected abstract O mapRow(ResultSet rs) throws Exception;
    protected abstract I getId(O object);

    @Override
    public Optional<O> get(I id)
    {
        final List<O> result = new ArrayList<>();

        db.executeQuery("SELECT * FROM " + table + " WHERE id=?", rs -> {
            if (rs.next()) result.add(mapRow(rs));
        }, id);

        return result.stream().findFirst();
    }

    @Override
    public Collection<O> all()
    {
        final List<O> result = new ArrayList<>();

        db.executeQuery("SELECT * FROM " + table, rs -> {
            while (rs.next()) result.add(mapRow(rs));
        });

        return result;
    }

    @Override
    public void delete(O object) {
        db.executeUpdate("DELETE FROM " + table + " WHERE id=?", getId(object));
    }

    @Override
    public boolean exists(I id)
    {
        final boolean[] exists = {false};

        db.executeQuery("SELECT 1 FROM " + table + " WHERE id=? LIMIT 1", rs -> {
            if (rs.next()) exists[0] = true;
        }, id);

        return exists[0];
    }
}
