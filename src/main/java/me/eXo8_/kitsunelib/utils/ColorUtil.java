package me.eXo8_.kitsunelib.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil
{
    public static String parse(String message)
    {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find())
        {
            String hexCode = matcher.group();
            String colorCode = hexCode.substring(1).replace("#", "x");

            char[] ch = colorCode.toCharArray();
            StringBuilder builder = new StringBuilder();

            for (char c : ch)
                builder.append("&").append(c);

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Component parse(Component component)
    {
        String message = component.toString();

        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find())
        {
            String hexCode = matcher.group();
            String colorCode = hexCode.substring(1);

            TextColor color = TextColor.fromHexString(colorCode);

            message = message.replace(hexCode, "§x" + colorCode.charAt(0) + colorCode.charAt(1) + colorCode.charAt(2) + colorCode.charAt(3) + colorCode.charAt(4) + colorCode.charAt(5));
            matcher = pattern.matcher(message);
        }

        return Component.text(message).style(Style.empty());
    }
}
