package me.eXo8_.kitsunelib.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SavedGUI extends GUI
{
    private final Map<UUID, SavedData> savedInventory = new HashMap<>();
    private final SaveType saveType;
    private final int[] customSlots;

    public SavedGUI(String title, int row, SaveType saveType, int... customSlots)
    {
        super(title, row);

        this.saveType = saveType;
        this.customSlots = customSlots;

        onEvent(InventoryOpenEvent.class, e-> {
            if (e.getPlayer() instanceof Player player) saveInventory(player);
        });

        onEvent(InventoryCloseEvent.class, e -> {
            if (e.getPlayer() instanceof Player player) restoreInventory(player);
        });
    }

    public void saveInventory(Player player)
    {
        ItemStack[] storage = null, armor = null;
        ItemStack offhand = null;

        switch (saveType) {

            case FULL ->
            {
                storage = player.getInventory().getStorageContents().clone();
                armor = player.getInventory().getArmorContents().clone();
                offhand = player.getInventory().getItemInOffHand().clone();
                clearInventory(player, true, true, true);
            }
            case STORAGE_ONLY ->
            {
                storage = player.getInventory().getStorageContents().clone();
                clearInventory(player, true, false, false);
            }
            case ARMOR_ONLY ->
            {
                armor = player.getInventory().getArmorContents().clone();
                clearInventory(player, false, true, false);
            }
            case OFFHAND_ONLY ->
            {
                offhand = player.getInventory().getItemInOffHand().clone();
                clearInventory(player, false, false, true);
            }
            case STORAGE_WITHOUT_ARMOR ->
            {
                storage = player.getInventory().getStorageContents().clone();
                offhand = player.getInventory().getItemInOffHand().clone();
                clearInventory(player, true, false, true);
            }
            case WITHOUT_OFFHAND ->
            {
                storage = player.getInventory().getStorageContents().clone();
                armor = player.getInventory().getArmorContents().clone();
                clearInventory(player, true, true, false);
            }
            case WITHOUT_ARMOR ->
            {
                storage = player.getInventory().getStorageContents().clone();
                offhand = player.getInventory().getItemInOffHand().clone();
                clearInventory(player, true, false, true);
            }
            case ARMOR_AND_OFFHAND ->
            {
                armor = player.getInventory().getArmorContents().clone();
                offhand = player.getInventory().getItemInOffHand().clone();
                clearInventory(player, true, true, true);
            }
            case STORAGE_AND_ARMOR ->
            {
                storage = player.getInventory().getStorageContents().clone();
                armor = player.getInventory().getArmorContents().clone();
                clearInventory(player, true, true, true);
            }
            case STORAGE_AND_OFFHAND ->
            {
                storage = player.getInventory().getStorageContents().clone();
                offhand = player.getInventory().getItemInOffHand().clone();
                clearInventory(player, true, true, true);
            }
            case CUSTOM ->
            {
                storage = player.getInventory().getStorageContents().clone();

                for (int slot : customSlots)
                {
                    if (slot < storage.length) storage[slot] = player.getInventory().getItem(slot);
                    player.getInventory().setItem(slot, null);
                }
            }
        }

        savedInventory.put(player.getUniqueId(), new SavedData(storage, armor, offhand));
    }

    public void restoreInventory(Player player)
    {
        SavedData data = savedInventory.remove(player.getUniqueId());
        if (data == null) return;

        switch (saveType) {
            case FULL, STORAGE_AND_ARMOR ->
            {
                if (data.storage != null) player.getInventory().setStorageContents(data.storage);
                if (data.armor != null) player.getInventory().setArmorContents(data.armor);
                if (data.offhand != null) player.getInventory().setItemInOffHand(data.offhand);
            }
            case STORAGE_ONLY -> {
                if (data.storage != null) player.getInventory().setStorageContents(data.storage);
            }
            case ARMOR_ONLY -> {
                if (data.armor != null) player.getInventory().setArmorContents(data.armor);
            }
            case OFFHAND_ONLY -> {
                if (data.offhand != null) player.getInventory().setItemInOffHand(data.offhand);
            }
            case STORAGE_WITHOUT_ARMOR, WITHOUT_ARMOR, STORAGE_AND_OFFHAND ->
            {
                if (data.storage != null) player.getInventory().setStorageContents(data.storage);
                if (data.offhand != null) player.getInventory().setItemInOffHand(data.offhand);
            }
            case WITHOUT_OFFHAND, ARMOR_AND_OFFHAND ->
            {
                if (data.storage != null) player.getInventory().setStorageContents(data.storage);
                if (data.armor != null) player.getInventory().setArmorContents(data.armor);

                if (saveType == SaveType.ARMOR_AND_OFFHAND && data.offhand != null)
                    player.getInventory().setItemInOffHand(data.offhand);
            }
            case CUSTOM ->
            {
                if (data.storage != null)
                    for (int slot : customSlots)
                        if (slot < data.storage.length) player.getInventory().setItem(slot, data.storage[slot]);
            }
        }
    }

    private void clearInventory(Player player, boolean storage, boolean armor, boolean offhand)
    {
        if (storage) player.getInventory().setStorageContents(new ItemStack[player.getInventory().getStorageContents().length]);
        if (armor) player.getInventory().setArmorContents(new ItemStack[4]);
        if (offhand) player.getInventory().setItemInOffHand(null);
    }

    private record SavedData(ItemStack[] storage, ItemStack[] armor, ItemStack offhand) {}
}
