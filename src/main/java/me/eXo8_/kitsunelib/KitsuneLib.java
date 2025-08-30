package me.eXo8_.kitsunelib;

import me.eXo8_.kitsunelib.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class KitsuneLib
{
    private static JavaPlugin plugin;


    public static void init(JavaPlugin plugin)
    {
        KitsuneLib.plugin = plugin;
        Logger.init(plugin.getLogger());

    }

    public static @NotNull JavaPlugin getPlugin() {
        return plugin;
    }
}