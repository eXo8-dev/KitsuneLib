package me.eXo8_.kitsunelib.gui.listener;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.gui.GUI;
import me.eXo8_.kitsunelib.gui.InputGUI;
import me.eXo8_.kitsunelib.gui.interfaces.SavedGUI;
import me.eXo8_.kitsunelib.gui.manager.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Consumer;

public class ListenerRegistry implements Listener
{
    public static void register() {
        Bukkit.getPluginManager().registerEvents(new ListenerRegistry(), KitsuneLib.getPlugin());
    }

    @EventHandler
    public void onDisablePlugin(PluginDisableEvent e)
    {
        if (e.getPlugin().equals(KitsuneLib.getPlugin()))
            for (var player : Bukkit.getOnlinePlayers())
            {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof GUI gui)
                {
                    if (gui instanceof SavedGUI)
                        InventoryManager.restore(player);

                    player.closeInventory();
                }

                var inputGUI = KitsuneLib.getInputGUI(player);

                if (inputGUI != null)
                    inputGUI.close();
            }

    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (e.getInventory().getHolder() instanceof GUI gui)
        {
            int slot = e.getRawSlot();

            for (Consumer<InventoryClickEvent> handler : gui.getGlobalClickHandlers())
                handler.accept(e);

            Consumer<InventoryClickEvent> handler = gui.getSlotEventMap().get(slot);
            if (handler != null) handler.accept(e);

            int playerSlot = slot - e.getView().getTopInventory().getSize();

            if (playerSlot >= 0 && playerSlot < player.getInventory().getSize())
            {
                Consumer<InventoryClickEvent> playerHandler = gui.getPlayerSlotEventMap().get(playerSlot);
                if (playerHandler != null) playerHandler.accept(e);
            }
        }

        InputGUI inputGUI = KitsuneLib.getInputGUI(player);
        if (inputGUI != null) e.setCancelled(true);
    }


    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof SavedGUI)
            InventoryManager.save((Player) e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        Player player = (Player) e.getPlayer();

        if (e.getInventory().getHolder() instanceof SavedGUI)
            InventoryManager.restore(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        InventoryManager.restore(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        InventoryManager.drop(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e)
    {
        if (e.getEntity() instanceof Player player)
            if (InventoryManager.hasSavedInventory(player)) e.setCancelled(true);
    }

    private boolean isSaved(Inventory inventory)
    {
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof SavedGUI;
    }

    private boolean hasOpenSavedGUI(Player player)
    {
        Inventory top = player.getOpenInventory().getTopInventory();
        InventoryHolder holder = top.getHolder();
        if (holder instanceof SavedGUI) return true;
        return KitsuneLib.getInputGUI(player) != null;
    }
}