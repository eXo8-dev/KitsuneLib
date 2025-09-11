package me.eXo8_.kitsunelib.gui;

import org.bukkit.entity.Player;

public class PlayerSavedGUI extends SavedGUI
{
    private final Player owner;

    public PlayerSavedGUI(Player owner, String title, int row, SaveType saveType, int... customSlots)
    {
        super(title, row, saveType, customSlots);
        this.owner = owner;
    }

    public void open() {
        super.open(owner);
    }

    public Player getOwner() {
        return owner;
    }
}