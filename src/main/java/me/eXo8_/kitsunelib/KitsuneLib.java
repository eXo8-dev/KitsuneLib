package me.eXo8_.kitsunelib;

import me.eXo8_.kitsunelib.gui.SavedGUI;
import me.eXo8_.kitsunelib.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class KitsuneLib
{
    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin)
    {
        KitsuneLib.plugin = plugin;
        Logger.init(plugin.getLogger());
        Logger.info("&aEnabled ");

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onDisablePlugin(PluginDisableEvent e)
            {
                if (e.getPlugin().equals(plugin))
                    for (var player : Bukkit.getOnlinePlayers())
                        if (player.getOpenInventory().getTopInventory().getHolder() instanceof SavedGUI saved)
                        {
                            saved.restoreInventory(player);
                            player.closeInventory();
                        }
            }
        }, plugin);
    }

    public static @NotNull JavaPlugin getPlugin() {
        return plugin;
    }
}