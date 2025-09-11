package me.eXo8_.kitsunelib.gui;

import org.bukkit.entity.Player;

public class PlayerGUI extends GUI
{
    private final Player owner;

    public PlayerGUI(Player owner, String title, int row)
    {
        super(title, row);
        this.owner = owner;
    }

    public void open() {
        owner.openInventory(getInventory());
    }

    public Player getOwner() {
        return owner;
    }
}
