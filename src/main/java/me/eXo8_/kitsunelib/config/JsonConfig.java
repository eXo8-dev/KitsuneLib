package me.eXo8_.kitsunelib.config;

import com.google.gson.*;
import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class JsonConfig implements Config
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected JsonObject config;
    private final File configFile;

    public JsonConfig(String fileName)
    {
        JavaPlugin plugin = KitsuneLib.getPlugin();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        this.configFile = new File(dataFolder, fileName);

        if (!this.configFile.exists())
            plugin.saveResource(fileName, false);

        reload();
    }

    @Override
    public void save() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            Logger.severe("Failed to save JSON config: " + configFile.getName());
        }
    }

    @Override
    public void reload()
    {
        try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8))
        {
            this.config = GSON.fromJson(reader, JsonObject.class);
            if (this.config == null) this.config = new JsonObject();
        }
        catch (IOException e)
        {
            Logger.severe("Failed to load JSON config: " + configFile.getName());
            this.config = new JsonObject();
        }

        disable();
        enable();
    }

    public boolean has(String key) {
        return config.has(key);
    }

    public @Nullable String getString(String key) {
        return has(key) ? config.get(key).getAsString() : null;
    }

    public @NotNull String getString(String key, @NotNull String def) {
        return has(key) ? config.get(key).getAsString() : def;
    }

    public int getInt(String key, int def) {
        return has(key) ? config.get(key).getAsInt() : def;
    }

    public long getLong(String key, long def) {
        return has(key) ? config.get(key).getAsLong() : def;
    }

    public double getDouble(String key, double def) {
        return has(key) ? config.get(key).getAsDouble() : def;
    }

    public boolean getBoolean(String key, boolean def) {
        return has(key) ? config.get(key).getAsBoolean() : def;
    }

    public @NotNull List<String> getStringList(String key)
    {
        if (!has(key) || !config.get(key).isJsonArray()) return Collections.emptyList();
        List<String> list = new ArrayList<>();
        config.getAsJsonArray(key).forEach(e -> list.add(e.getAsString()));
        return list;
    }

    public @NotNull List<Integer> getIntList(String key)
    {
        if (!has(key) || !config.get(key).isJsonArray()) return Collections.emptyList();
        List<Integer> list = new ArrayList<>();
        config.getAsJsonArray(key).forEach(e -> list.add(e.getAsInt()));
        return list;
    }

    public @NotNull List<Double> getDoubleList(String key)
    {
        if (!has(key) || !config.get(key).isJsonArray()) return Collections.emptyList();
        List<Double> list = new ArrayList<>();
        config.getAsJsonArray(key).forEach(e -> list.add(e.getAsDouble()));
        return list;
    }

    public @NotNull List<Boolean> getBooleanList(String key)
    {
        if (!has(key) || !config.get(key).isJsonArray()) return Collections.emptyList();
        List<Boolean> list = new ArrayList<>();
        config.getAsJsonArray(key).forEach(e -> list.add(e.getAsBoolean()));
        return list;
    }

    public void set(String key, @Nullable Object value)
    {
        if (value == null) config.remove(key);
        else config.add(key, GSON.toJsonTree(value));
    }

    public @NotNull Set<String> getKeys() {
        return config.keySet();
    }

    public @Nullable Object get(String key)
    {
        if (!has(key)) return null;
        JsonElement el = config.get(key);
        if (el.isJsonPrimitive())
        {
            JsonPrimitive p = el.getAsJsonPrimitive();

            if (p.isBoolean()) return p.getAsBoolean();
            if (p.isNumber()) return p.getAsNumber();
            if (p.isString()) return p.getAsString();
        }

        if (el.isJsonArray()) return el.getAsJsonArray();
        if (el.isJsonObject()) return el.getAsJsonObject();

        return null;
    }

    public JsonObject getRaw() {
        return config;
    }
}
