package me.eXo8_.kitsunelib.builder.impl;

import me.eXo8_.kitsunelib.builder.Builder;
import me.eXo8_.kitsunelib.utils.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

public class TextBuilder implements Builder<TextComponent> {

    private final List<TextComponent> components = new ArrayList<>();

    public TextBuilder add(String text)
    {
        components.add(Component.text(ColorUtil.parse(text)));
        return this;
    }

    public TextBuilder add(String text, TextColor color)
    {
        components.add(Component.text(ColorUtil.parse(text), color));
        return this;
    }

    public TextBuilder add(String text, TextColor color, String hoverText, String command)
    {
        TextComponent comp = Component.text(ColorUtil.parse(text), color)
                .hoverEvent(HoverEvent.showText(Component.text(ColorUtil.parse(hoverText))))
                .clickEvent(ClickEvent.runCommand(command));
        components.add(comp);
        return this;
    }

    public TextBuilder hover(String hoverText)
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1,
                    last.hoverEvent(HoverEvent.showText(Component.text(ColorUtil.parse(hoverText)))));
        }
        return this;
    }

    public TextBuilder click(String command)
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.clickEvent(ClickEvent.runCommand(command)));
        }
        return this;
    }

    public TextBuilder suggest(String command)
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.clickEvent(ClickEvent.suggestCommand(command)));
        }
        return this;
    }

    public TextBuilder openUrl(String url)
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.clickEvent(ClickEvent.openUrl(url)));
        }
        return this;
    }

    public TextBuilder bold()
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.decorate(TextDecoration.BOLD));
        }
        return this;
    }

    public TextBuilder italic()
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.decorate(TextDecoration.ITALIC));
        }
        return this;
    }

    public TextBuilder underline()
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.decorate(TextDecoration.UNDERLINED));
        }
        return this;
    }

    public TextBuilder strikethrough()
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.decorate(TextDecoration.STRIKETHROUGH));
        }
        return this;
    }

    public TextBuilder obfuscated()
    {
        if (!components.isEmpty())
        {
            TextComponent last = components.get(components.size() - 1);
            components.set(components.size() - 1, last.decorate(TextDecoration.OBFUSCATED));
        }
        return this;
    }

    @Override
    public TextComponent build()
    {
        if (components.isEmpty()) return Component.text("");

        TextComponent first = components.get(0);

        for (int i = 1; i < components.size(); i++)
            first = first.append(components.get(i));

        return first;
    }

    public TextBuilder clear()
    {
        components.clear();
        return this;
    }
}