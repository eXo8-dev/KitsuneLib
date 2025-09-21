package me.eXo8_.kitsunelib.database.dao;

import java.util.HashMap;
import java.util.Map;

public class DataBundle
{
    private final Map<Class<?>, Object> data = new HashMap<>();

    public void put(Class<?> clazz, Object value) {
        this.data.put(clazz, value);
    }

    public <T> T get(Class<T> clazz) {
        return (T) data.get(clazz);
    }

    public Map<Class<?>, Object> getAll() {
        return Map.copyOf(data);
    }
}
