package me.eXo8_.kitsunelib.gui;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GUI implements Listener, InventoryHolder
{
    private final Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> slotEventMap = new HashMap<>();
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> eventHandlers = new HashMap<>();
    private  Consumer<InventoryClickEvent> globalClickEvent;
    private GUI parent;

    public GUI(String title, int row)
    {
        this.inventory = Bukkit.createInventory(this, row * 9, ColorUtil.parse(title));
        Bukkit.getPluginManager().registerEvents(this, KitsuneLib.getPlugin());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(this.inventory)) return;

        int slot = e.getRawSlot();
        if (globalClickEvent != null) globalClickEvent.accept(e);

        Consumer<InventoryClickEvent> handler = slotEventMap.get(slot);
        if (handler != null) handler.accept(e);
    }

    public <T extends Event> GUI onEvent(Class<T> eventClass, Consumer<T> handler)
    {
        eventHandlers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(handler);
        return this;
    }

    @EventHandler
    public void handleGenericEvents(Event e)
    {
        List<Consumer<? extends Event>> handlers = eventHandlers.get(e.getClass());

        if (handlers != null)
            for (Consumer<? extends Event> h : handlers)
                try {((Consumer<Event>) h).accept(e);} catch (ClassCastException ignored) {}
    }

    public GUI setVisualItem(int slot, ItemStack item)
    {
        inventory.setItem(slot, item);
        return this;
    }

    public GUI setVisualItem(int[] slots, ItemStack item)
    {
        for (int slot : slots)
            setVisualItem(slot, item);
        return this;
    }

    public GUI setLogicalItem(int slot, ItemStack item, Consumer<InventoryClickEvent> e)
    {
        inventory.setItem(slot, item);
        slotEventMap.put(slot, e);
        return this;
    }

    public GUI setLogicalItem(int[] slots, ItemStack item, Consumer<InventoryClickEvent> e)
    {
        for (int slot : slots)
            setLogicalItem(slot, item, e);
        return this;
    }

    public GUI setGlobalСlickEvent(int slot, ItemStack item, Consumer<InventoryClickEvent> e)
    {
        inventory.setItem(slot, item);
        globalClickEvent = e;
        return this;
    }

    public GUI setGlobalСlickEvent(Consumer<InventoryClickEvent> e)
    {
        globalClickEvent = e;
        return this;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void openParent(Player player) {
        if (parent != null) parent.open(player);
    }

    public GUI getParent() {
        return parent;
    }

    public void setParent(GUI parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
