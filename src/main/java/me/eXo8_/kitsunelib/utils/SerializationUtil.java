package me.eXo8_.kitsunelib.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class SerializationUtil
{
    public static String itemStackToBase64(ItemStack item) {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)
        )
        {
            dataOutput.writeObject(item);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack itemStackFromBase64(String base64) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)
        )
        {return (ItemStack) dataInput.readObject();}
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String itemStackToYaml(ItemStack item)
    {
        if (item == null)
            return null;

        YamlConfiguration config = new YamlConfiguration();
        config.set("item", item);

        return config.saveToString();
    }

    public static ItemStack itemStackFromYaml(String string)
    {
        if (string == null || string.isEmpty())
            return null;

        YamlConfiguration config = new YamlConfiguration();

        try
        {
            config.loadFromString(string);
            return config.getItemStack("item");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String serializeLocation(Location loc)
    {
        if (loc == null) return null;

        return loc.getWorld().getName() + ";"
               + loc.getX() + ";"
               + loc.getY() + ";"
               + loc.getZ() + ";"
               + loc.getYaw() + ";"
               + loc.getPitch();
    }

    public static Location deserializeLocation(String str)
    {
        if (str == null || str.isEmpty()) return null;

        String[] parts = str.split(";");
        if (parts.length != 6) return null;

        try
        {
            String worldName = parts[0];
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
