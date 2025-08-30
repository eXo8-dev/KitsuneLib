package me.eXo8_.kitsunelib.gui;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
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
    private final Map<Integer, Consumer<InventoryClickEvent>> eventMap = new HashMap<>();
    private  Consumer<InventoryClickEvent> globalEvent;
    private final List<EventSubscriber> subscribers = new ArrayList<>();
    private GUI parent;

    public GUI(String title, int row)
    {
        this.inventory = Bukkit.createInventory(this, row * 9, ColorUtil.parse(title));
        Bukkit.getPluginManager().registerEvents(this, KitsuneLib.getPlugin());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;

        int slot = e.getRawSlot();

        if (globalEvent != null) globalEvent.accept(e);

        Consumer<InventoryClickEvent> handler = eventMap.get(slot);
        if (handler != null) handler.accept(e);

        for (EventSubscriber subscriber : subscribers) subscriber.onClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onClose(e);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onOpen(e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onDrag(e);
    }

    @EventHandler
    public void onMoveItem(InventoryMoveItemEvent e)
    {
        if (!e.getInitiator().equals(this.inventory) && !e.getDestination().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onMoveItem(e);
    }

    @EventHandler
    public void onPickupItem(InventoryPickupItemEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onPickupItem(e);
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onInteract(e);
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent e)
    {
        if (!e.getInventory().equals(this.inventory)) return;
        for (EventSubscriber subscriber : subscribers) subscriber.onCreative(e);
    }

    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent e)
    {
        if (e.getPlayer().getOpenInventory().getTopInventory().equals(this.inventory))
            for (EventSubscriber subscriber : subscribers) subscriber.onPlayerSwap(e);
    }

    public GUI addSubscriber(EventSubscriber subscriber)
    {
        subscribers.add(subscriber);
        return this;
    }

    public GUI removeSubscriber(EventSubscriber subscriber)
    {
        subscribers.remove(subscriber);
        return this;
    }

    public GUI setVisualItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    public GUI setVisualItem(int[] slots, ItemStack item) {
        for (int slot : slots) setVisualItem(slot, item);
        return this;
    }

    public GUI setLogicalItem(int slot, ItemStack item, Consumer<InventoryClickEvent> e)
    {
        inventory.setItem(slot, item);
        eventMap.put(slot, e);
        return this;
    }

    public GUI setLogicalItem(int[] slots, ItemStack item, Consumer<InventoryClickEvent> e)
    {
        for (int slot : slots) setLogicalItem(slot, item, e);
        return this;
    }

    public GUI setGlobalEvent(int slot, ItemStack item, Consumer<InventoryClickEvent> e)
    {
        inventory.setItem(slot, item);
        globalEvent = e;
        return this;
    }

    public GUI setGlobalEvent(Consumer<InventoryClickEvent> e) {
        globalEvent = e;
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

    public interface EventSubscriber
    {
        default void onClick(InventoryClickEvent e) {}
        default void onClose(InventoryCloseEvent e) {}
        default void onOpen(InventoryOpenEvent e) {}
        default void onDrag(InventoryDragEvent e) {}
        default void onMoveItem(InventoryMoveItemEvent e) {}
        default void onPickupItem(InventoryPickupItemEvent e) {}
        default void onInteract(InventoryInteractEvent e) {}
        default void onCreative(InventoryCreativeEvent e) {}
        default void onPlayerSwap(PlayerSwapHandItemsEvent e) {}
    }
}
