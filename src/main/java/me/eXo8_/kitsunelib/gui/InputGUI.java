package me.eXo8_.kitsunelib.gui;

import me.eXo8_.kitsunelib.KitsuneLib;
import me.eXo8_.kitsunelib.gui.interfaces.SavedGUI;
import me.eXo8_.kitsunelib.gui.manager.InventoryManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class InputGUI
{
    private final Player player;
    private String text = "";
    private ItemStack leftItem;
    private ItemStack rightItem;
    private ItemStack outputItem;

    private BiFunction<Integer, AnvilGUI.StateSnapshot, List<AnvilGUI.ResponseAction>> clickHandler;
    private AnvilGUI.ClickHandler clickHandlerAsync;
    private Runnable onClose;

    private final List<BottomSlot> bottomSlots = new ArrayList<>();

    private static class BottomSlot
    {
        int slot;
        ItemStack item;
        Consumer<InventoryClickEvent> handler;

        BottomSlot(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
            this.slot = slot;
            this.item = item;
            this.handler = handler;
        }
    }

    public InputGUI(Player player) {
        this.player = player;
    }

    public InputGUI setText(String text) {
        this.text = text;
        return this;
    }

    public InputGUI setLeftItem(ItemStack item) {
        this.leftItem = item;
        return this;
    }

    public InputGUI setRightItem(ItemStack item) {
        this.rightItem = item;
        return this;
    }

    public InputGUI setOutputItem(ItemStack item) {
        this.outputItem = item;
        return this;
    }

    public InputGUI onClick(BiFunction<Integer, AnvilGUI.StateSnapshot, List<AnvilGUI.ResponseAction>> handler) {
        this.clickHandler = handler;
        return this;
    }

    public InputGUI onClickAsync(AnvilGUI.ClickHandler handler) {
        this.clickHandlerAsync = handler;
        return this;
    }

    public InputGUI onClose(Runnable callback) {
        this.onClose = callback;
        return this;
    }

    public void bottom(Player player, int slot, ItemStack item) {
        bottomSlots.add(new BottomSlot(slot, item, null));
    }

    public void bottom(Player player, int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        bottomSlots.add(new BottomSlot(slot, item, handler));
    }

    public void bottom(Player player, int[] slots, ItemStack item) {
        for (int slot : slots) bottom(player, slot, item);
    }

    public void bottom(Player player, int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) bottom(player, slot, item, handler);
    }
    public void open()
    {
        player.closeInventory();

        KitsuneLib.syncLater(() -> {
            if (this instanceof SavedGUI)
                InventoryManager.save(player);

            KitsuneLib.registerInputGUI(player, this);

            if (!bottomSlots.isEmpty())
                leftItem = bottomSlots.getFirst().item;

            AnvilGUI.Builder builder = new AnvilGUI.Builder()
                    .plugin(KitsuneLib.getPlugin())
                    .text(text)
                    .itemLeft(leftItem)
                    .itemRight(rightItem)
                    .itemOutput(outputItem);

            if (clickHandlerAsync != null)
                builder.onClickAsync(clickHandlerAsync);
            if (clickHandler != null)
                builder.onClick((slot, state) -> clickHandler.apply(slot, state));

            builder.onClose(state -> close());

            builder.open(player);
        }, 1);
    }

    public void close()
    {
        if (onClose != null)
            onClose.run();

        if (this instanceof SavedGUI)
            InventoryManager.restore(player);

        KitsuneLib.unregisterInputGUI(player);

        player.closeInventory();
    }


}