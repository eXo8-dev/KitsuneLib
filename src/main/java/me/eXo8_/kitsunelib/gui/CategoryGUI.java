package me.eXo8_.kitsunelib.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CategoryGUI extends GUI {

    private final Map<Integer, GUI> subMenus = new HashMap<>();

    public CategoryGUI(String title, int rows) {
        super(title, rows);
    }

    public CategoryGUI addCategory(int slot, ItemStack item, GUI subGUI)
    {
        setLogicalItem(slot, item, e -> {
            e.setCancelled(true);
            subGUI.setParent(this);
            subGUI.open((Player) e.getWhoClicked());
        });
        subMenus.put(slot, subGUI);
        return this;
    }

    public CategoryGUI addCategory(int slot, GUI subGUI) {
        slotEventMap.put(slot, e -> {
            e.setCancelled(true);
            subGUI.setParent(this);
            subGUI.open((Player) e.getWhoClicked());
        });
        subMenus.put(slot, subGUI);
        return this;
    }

    public GUI getSubMenu(int slot) {
        return subMenus.get(slot);
    }
}
