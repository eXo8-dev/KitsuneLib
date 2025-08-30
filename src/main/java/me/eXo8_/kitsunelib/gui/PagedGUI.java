package me.eXo8_.kitsunelib.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PagedGUI extends GUI
{
    private final List<ItemStack> contents = new ArrayList<>();
    private int page = 0;
    private final int itemsPerPage;

    public PagedGUI(String title, int rows)
    {
        super(title, rows);
        this.itemsPerPage = (rows - 1) * 9;
    }

    public void setContents(List<ItemStack> items)
    {
        contents.clear();
        contents.addAll(items);
        page = 0;
        renderPage();
    }

    private void renderPage()
    {
        getInventory().clear();

        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, contents.size());

        for (int i = start; i < end; i++)
        {
            int slot = i - start;
            setVisualItem(slot, contents.get(i));
        }
    }

    @Override
    public void open(Player player)
    {
        renderPage();
        super.open(player);
    }

    public boolean nextPage()
    {
        if (page + 1 < getMaxPages())
        {
            page++;
            renderPage();
            return true;
        }
        return false;
    }

    public boolean previousPage()
    {
        if (page > 0)
        {
            page--;
            renderPage();
            return true;
        }
        return false;
    }

    public int getPage() {
        return page;
    }

    public int getMaxPages() {
        return (int) Math.ceil((double) contents.size() / itemsPerPage);
    }
}
