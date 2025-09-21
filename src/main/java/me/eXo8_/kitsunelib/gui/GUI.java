package me.eXo8_.kitsunelib.gui;

import me.eXo8_.kitsunelib.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

abstract public class GUI implements Listener, InventoryHolder
{
    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> slotEventMap = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> playerSlotEventMap = new HashMap<>();
    private final List<Consumer<InventoryClickEvent>> globalClickHandlers = new ArrayList<>();
    private GUI parent;
    private Runnable onOpen;

    private Player owner;

    public GUI(String title, int row) {
        this.inventory = Bukkit.createInventory(this, row * 9, ColorUtil.parse(title));
    }

    public GUI(Player player, String title, int row)
    {
        this.inventory = Bukkit.createInventory(this, row * 9, ColorUtil.parse(title));
        this.owner = player;
    }

    public GUI setItemInternal(Inventory target, int slot, ItemStack item, Consumer<InventoryClickEvent> clickHandler, boolean isPlayerSlot)
    {
        if (target != null && item != null) target.setItem(slot, item);

        if (clickHandler != null)
        {
            if (isPlayerSlot) playerSlotEventMap.put(slot, clickHandler);
            else slotEventMap.put(slot, clickHandler);
        }

        return this;
    }

    public GUI top(int slot, ItemStack item) {
        return setItemInternal(inventory, slot, item, null, false);
    }

    public GUI top(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        return setItemInternal(inventory, slot, item, handler, false);
    }

    public GUI top(int[] slots, ItemStack item)
    {
        for (int slot : slots) top(slot, item);
        return this;
    }

    public GUI top(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler)
    {
        for (int slot : slots) top(slot, item, handler);
        return this;
    }

    public void bottom(Player player, int slot, ItemStack item) {
        setItemInternal(player.getInventory(), slot, item, null, true);
    }

    public void bottom(Player player, int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItemInternal(player.getInventory(), slot, item, handler, true);
    }

    public GUI bottom(Player player, int[] slots, ItemStack item)
    {
        for (int slot : slots)
            bottom(player, slot, item);
        return this;
    }

    public GUI bottom(Player player, int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler)
    {
        for (int slot : slots)
            bottom(player, slot, item, handler);
        return this;
    }

    public void bottom(int slot, ItemStack item) {
        setItemInternal(owner.getInventory(), slot, item, null, true);
    }

    public void bottom(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setItemInternal(owner.getInventory(), slot, item, handler, true);
    }

    public GUI bottom(int[] slots, ItemStack item)
    {
        for (int slot : slots)
            bottom(owner, slot, item);
        return this;
    }

    public GUI bottom(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler)
    {
        for (int slot : slots)
            bottom(owner, slot, item, handler);
        return this;
    }

    public GUI globalClick(Consumer<InventoryClickEvent> handler)
    {
        globalClickHandlers.add(handler);
        return this;
    }

    public void open(Player player)
    {
        if (isPlayerOnly() && !player.equals(owner)) return;
        player.openInventory(inventory);
        if (onOpen != null) onOpen.run();
    }

    public void onOpen(Runnable runnable) {
        this.onOpen = runnable;
    }

    public void open()
    {
        if (owner != null)
            open(owner);
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

    public Map<Integer, Consumer<InventoryClickEvent>> getSlotEventMap() {
        return slotEventMap;
    }

    public Map<Integer, Consumer<InventoryClickEvent>> getPlayerSlotEventMap() {
        return playerSlotEventMap;
    }

    public List<Consumer<InventoryClickEvent>> getGlobalClickHandlers() {
        return globalClickHandlers;
    }

    public boolean isPlayerOnly() {
        return owner != null;
    }

    public Player getOwner() {
        return owner;
    }

    public Runnable getOnOpen() {
        return onOpen;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}