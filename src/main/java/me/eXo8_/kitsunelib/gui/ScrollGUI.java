package me.eXo8_.kitsunelib.gui;

import me.eXo8_.kitsunelib.gui.object.ScrollDirection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScrollGUI extends GUI
{
    private final Map<Point, ItemStack> canvas = new HashMap<>();

    private final int canvasWidth;
    private final int canvasHeight;

    private final int viewportWidth;
    private final int viewportHeight;

    private int offsetX = 0;
    private int offsetY = 0;

    private int scrollStepX = 1;
    private int scrollStepY = 1;

    public ScrollGUI(String title, int rows, int canvasWidth, int canvasHeight)
    {
        super(title, rows);

        this.viewportWidth = 9;
        this.viewportHeight = rows - 1;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public void setItem(int x, int y, ItemStack item)
    {
        if (x < 0 || y < 0 || x >= canvasWidth || y >= canvasHeight) return;
        canvas.put(new Point(x, y), item);
    }

    private void render()
    {
        getInventory().clear();

        for (int y = 0; y < viewportHeight; y++)
        {
            for (int x = 0; x < viewportWidth; x++)
            {
                int worldX = offsetX + x;
                int worldY = offsetY + y;

                ItemStack item = canvas.get(new Point(worldX, worldY));

                if (item != null)
                {
                    int slot = y * 9 + x;
                    top(slot, item);
                }
            }
        }
    }

    @Override
    public void open(Player player)
    {
        render();
        super.open(player);
    }

    public void scroll(ScrollDirection dir) {
        scroll(dir, dir == ScrollDirection.LEFT || dir == ScrollDirection.RIGHT ? scrollStepX : scrollStepY);
    }

    public void scroll(ScrollDirection dir, int step)
    {
        switch (dir)
        {
            case UP -> { if (offsetY - step >= 0) offsetY -= step; }
            case DOWN -> { if (offsetY + viewportHeight + step <= canvasHeight) offsetY += step; }
            case LEFT -> { if (offsetX - step >= 0) offsetX -= step; }
            case RIGHT -> { if (offsetX + viewportWidth + step <= canvasWidth) offsetX += step; }
        }
        render();
    }

    public void setScrollStepX(int step) {
        this.scrollStepX = Math.max(1, step);
    }

    public void setScrollStepY(int step) {
        this.scrollStepY = Math.max(1, step);
    }

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
    public int getScrollStepX() { return scrollStepX; }
    public int getScrollStepY() { return scrollStepY; }
}