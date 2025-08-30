package me.eXo8_.kitsunelib.config;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.logger.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import net.kyori.adventure.text.serializer.ComponentEncoder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract public class YamlConfig implements Config
{
    protected FileConfiguration config;
    private final File configFile;

    public YamlConfig(String configName)
    {
        JavaPlugin plugin = KitsuneLib.getPlugin();
        File dataFolder = plugin.getDataFolder();

        if (!dataFolder.exists())
            dataFolder.mkdirs();

        this.configFile = new File(dataFolder, configName);

        if (!this.configFile.exists())
            plugin.saveResource(configName, false);

        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    @Override
    public void save()
    {
        try { config.save(configFile); }
        catch (IOException e) { Logger.severe("Failed to save config: " + configFile.getName()); }
    }

    @Override
    public void reload()
    {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        disable();
        enable();
    }

    public @NotNull List<Long> getLongList(@NotNull String path) {
        return config.getLongList(path);
    }

    public @Nullable Color getColor(@NotNull String path) {
        return config.getColor(path);
    }

    @Contract("_, !null -> !null")
    public @Nullable Component getRichMessage(@NotNull String path, @Nullable Component fallback) {
        return config.getRichMessage(path, fallback);
    }

    @Contract("_, _, !null -> !null")
    public <T> @Nullable T getObject(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return config.getObject(path, clazz, def);
    }

    @Contract("_, !null -> !null")
    public @Nullable Vector getVector(@NotNull String path, @Nullable Vector def) {
        return config.getVector(path, def);
    }

    public boolean isConfigurationSection(@NotNull String path) {
        return config.isConfigurationSection(path);
    }

    public @Nullable List<?> getList(@NotNull String path) {
        return config.getList(path);
    }

    public int getInt(@NotNull String path) {
        return config.getInt(path);
    }

    public boolean isList(@NotNull String path) {
        return config.isList(path);
    }

    public <T extends ConfigurationSerializable> @Nullable T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        return config.getSerializable(path, clazz);
    }

    @Nullable
    public Object get(@NotNull String path) {
        return config.get(path);
    }

    public boolean isString(@NotNull String path) {
        return config.isString(path);
    }

    public <C extends Component> void setComponent(@NotNull String path, @NotNull ComponentEncoder<C, String> encoder, @Nullable C value) {
        config.setComponent(path, encoder, value);
    }

    public boolean isItemStack(@NotNull String path) {
        return config.isItemStack(path);
    }

    public @Nullable Component getRichMessage(@NotNull String path) {
        return config.getRichMessage(path);
    }

    public void addDefaults(@NotNull Map<String, Object> defaults) {
        config.addDefaults(defaults);
    }

    public @NotNull ConfigurationSection createSection(@NotNull String path) {
        return config.createSection(path);
    }

    public @Nullable Location getLocation(@NotNull String path) {
        return config.getLocation(path);
    }

    public @NotNull List<String> getInlineComments(@NotNull String path) {
        return config.getInlineComments(path);
    }

    public void addDefaults(@NotNull Configuration defaults) {
        config.addDefaults(defaults);
    }

    public boolean isInt(@NotNull String path) {
        return config.isInt(path);
    }

    @Contract("_, _, !null -> !null")
    public <T extends ConfigurationSerializable> @Nullable T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return config.getSerializable(path, clazz, def);
    }

    public int getInt(@NotNull String path, int def) {
        return config.getInt(path, def);
    }

    @Contract("_, _, !null -> !null")
    public <C extends Component> @Nullable C getComponent(@NotNull String path, @NotNull ComponentDecoder<? super String, C> decoder, @Nullable C fallback) {
        return config.getComponent(path, decoder, fallback);
    }

    public @NotNull List<String> getStringList(@NotNull String path) {
        return config.getStringList(path);
    }

    public void setDefaults(@NotNull Configuration defaults) {
        config.setDefaults(defaults);
    }

    public @NotNull List<Character> getCharacterList(@NotNull String path) {
        return config.getCharacterList(path);
    }

    @Contract("_, !null -> !null")
    public @Nullable OfflinePlayer getOfflinePlayer(@NotNull String path, @Nullable OfflinePlayer def) {
        return config.getOfflinePlayer(path, def);
    }

    public @Nullable Vector getVector(@NotNull String path) {
        return config.getVector(path);
    }

    public @NotNull Map<String, Object> getValues(boolean deep) {
        return config.getValues(deep);
    }

    public @NotNull FileConfigurationOptions options() {
        return config.options();
    }

    public @Nullable Configuration getDefaults() {
        return config.getDefaults();
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public boolean isColor(@NotNull String path) {
        return config.isColor(path);
    }

    public @NotNull Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public @NotNull List<Map<?, ?>> getMapList(@NotNull String path) {
        return config.getMapList(path);
    }

    public boolean getBoolean(@NotNull String path) {
        return config.getBoolean(path);
    }

    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        return config.getBooleanList(path);
    }

    public @Nullable ConfigurationSection getParent() {
        return config.getParent();
    }

    public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path) {
        return config.getConfigurationSection(path);
    }

    @Contract("_, !null -> !null")
    public @Nullable List<?> getList(@NotNull String path, @Nullable List<?> def) {
        return config.getList(path, def);
    }

    @NotNull
    public String saveToString() {
        return config.saveToString();
    }

    public double getDouble(@NotNull String path) {
        return config.getDouble(path);
    }

    public boolean contains(@NotNull String path, boolean ignoreDefault) {
        return config.contains(path, ignoreDefault);
    }

    public @NotNull List<Float> getFloatList(@NotNull String path) {
        return config.getFloatList(path);
    }

    public void addDefault(@NotNull String path, @Nullable Object value) {
        config.addDefault(path, value);
    }

    public @Nullable OfflinePlayer getOfflinePlayer(@NotNull String path) {
        return config.getOfflinePlayer(path);
    }

    public void save(@NotNull String file) throws IOException {
        config.save(file);
    }

    public @Nullable ConfigurationSection getDefaultSection() {
        return config.getDefaultSection();
    }

    public boolean isBoolean(@NotNull String path) {
        return config.isBoolean(path);
    }

    public boolean contains(@NotNull String path) {
        return config.contains(path);
    }

    public void set(@NotNull String path, @Nullable Object value) {
        config.set(path, value);
    }

    @Contract("_, !null -> !null")
    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        return config.getString(path, def);
    }

    @Contract("_, !null -> !null")
    public @Nullable Color getColor(@NotNull String path, @Nullable Color def) {
        return config.getColor(path, def);
    }

    @Contract("_, !null -> !null")
    public @Nullable ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        return config.getItemStack(path, def);
    }

    public void load(@NotNull Reader reader) throws IOException, InvalidConfigurationException {
        config.load(reader);
    }

    @Contract("_, !null -> !null")
    @Nullable
    public Object get(@NotNull String path, @Nullable Object def) {
        return config.get(path, def);
    }

    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        config.setInlineComments(path, comments);
    }

    @NotNull
    public String getCurrentPath() {
        return config.getCurrentPath();
    }

    public boolean isLocation(@NotNull String path) {
        return config.isLocation(path);
    }

    public void load(@NotNull File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        config.load(file);
    }

    public @NotNull List<Byte> getByteList(@NotNull String path) {
        return config.getByteList(path);
    }

    public double getDouble(@NotNull String path, double def) {
        return config.getDouble(path, def);
    }

    public boolean isVector(@NotNull String path) {
        return config.isVector(path);
    }

    public boolean isSet(@NotNull String path) {
        return config.isSet(path);
    }

    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        config.loadFromString(contents);
    }

    public @Nullable Configuration getRoot() {
        return config.getRoot();
    }

    public long getLong(@NotNull String path) {
        return config.getLong(path);
    }

    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        return config.getIntegerList(path);
    }

    public @NotNull List<Short> getShortList(@NotNull String path) {
        return config.getShortList(path);
    }

    public @Nullable ItemStack getItemStack(@NotNull String path) {
        return config.getItemStack(path);
    }

    public void load(@NotNull String file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        config.load(file);
    }

    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        config.setComments(path, comments);
    }

    @NotNull
    public String getName() {
        return config.getName();
    }

    public boolean isDouble(@NotNull String path) {
        return config.isDouble(path);
    }

    @Contract("_, !null -> !null")
    public @Nullable Location getLocation(@NotNull String path, @Nullable Location def) {
        return config.getLocation(path, def);
    }

    public void setRichMessage(@NotNull String path, @Nullable Component value) {
        config.setRichMessage(path, value);
    }

    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        return config.getDoubleList(path);
    }

    public <T> @Nullable T getObject(@NotNull String path, @NotNull Class<T> clazz) {
        return config.getObject(path, clazz);
    }

    @Nullable
    public String getString(@NotNull String path) {
        return config.getString(path);
    }

    public boolean isLong(@NotNull String path) {
        return config.isLong(path);
    }

    public @NotNull ConfigurationSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return config.createSection(path, map);
    }

    public @NotNull List<String> getComments(@NotNull String path) {
        return config.getComments(path);
    }

    public long getLong(@NotNull String path, long def) {
        return config.getLong(path, def);
    }

    public boolean isOfflinePlayer(@NotNull String path) {
        return config.isOfflinePlayer(path);
    }

    public <C extends Component> @Nullable C getComponent(@NotNull String path, @NotNull ComponentDecoder<? super String, C> decoder) {
        return config.getComponent(path, decoder);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
