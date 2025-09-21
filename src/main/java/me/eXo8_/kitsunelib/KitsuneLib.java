package me.eXo8_.kitsunelib;

import me.eXo8_.kitsunelib.gui.InputGUI;
import me.eXo8_.kitsunelib.gui.listener.ListenerRegistry;
import me.eXo8_.kitsunelib.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KitsuneLib
{
    private static JavaPlugin plugin;
    private static final Map<UUID, InputGUI> activeInputGUIs = new HashMap<>();

    public static void init(JavaPlugin plugin)
    {
        KitsuneLib.plugin = plugin;
        Logger.init(plugin.getLogger());
        Logger.info("&aEnabled ");

        ListenerRegistry.register();
    }

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void registerInputGUI(Player player, InputGUI gui) {
        activeInputGUIs.put(player.getUniqueId(), gui);
    }

    public static InputGUI getInputGUI(Player player) {
        return activeInputGUIs.get(player.getUniqueId());
    }

    public static void unregisterInputGUI(Player player) {
        activeInputGUIs.remove(player.getUniqueId());
    }

    public static @NotNull JavaPlugin getPlugin() {
        return plugin;
    }

    public static void syncLater(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static void asyncLater(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }
}