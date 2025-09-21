package me.eXo8_.kitsunelib.gui.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InventoryManager
{
    private static final Map<UUID, ItemStack[]> storages = new HashMap<>();

    private InventoryManager() {}

    public static void save(Player player)
    {
        storages.put(player.getUniqueId(), player.getInventory().getStorageContents());
        clear(player);
    }

    public static void clear(Player player) {
        player.getInventory().setStorageContents(new ItemStack[player.getInventory().getStorageContents().length]);
    }

    public static void restore(Player player) {
        ItemStack[] storage = storages.remove(player.getUniqueId());
        if (storage != null) player.getInventory().setStorageContents(storage);
    }

    public static void drop(UUID id, Location location) {
        ItemStack[] storage = storages.remove(id);
        if (storage == null) return;
        for (ItemStack item : storage) {
            if (item != null) location.getWorld().dropItemNaturally(location, item);
        }
    }

    public static void delete(Player player) {
        storages.remove(player.getUniqueId());
    }

    public static boolean hasSavedInventory(Player player) {
        return storages.containsKey(player.getUniqueId());
    }

    public static void openSavedGUI(Player player, Runnable openInventory)
    {
        if (!storages.containsKey(player.getUniqueId())) save(player);
        else clear(player);

        if (openInventory != null)
            openInventory.run();
    }


    public static void closeSavedGUI(Player player) {
        restore(player);
    }

    public static void cleanupOnQuit(Player player)
    {
        if (hasSavedInventory(player))
            restore(player);
    }

    public static void dropOnDeath(Player player)
    {
        if (hasSavedInventory(player))
            drop(player.getUniqueId(), player.getLocation());
    }
}

