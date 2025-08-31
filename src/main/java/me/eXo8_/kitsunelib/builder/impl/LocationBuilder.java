package me.eXo8_.kitsunelib.builder.impl;

import me.eXo8_.kitsunelib.builder.Builder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LocationBuilder implements Builder<Location>
{
    private final String world;
    private double x, y, z;
    private float yaw, pitch;

    public LocationBuilder(String worldName) {
        this.world = worldName;
    }

    public LocationBuilder(World world) {
        this.world = world.getName();
    }

    public LocationBuilder x(double x) {
        this.x = x;
        return this;
    }

    public LocationBuilder y(double y) {
        this.y = y;
        return this;
    }

    public LocationBuilder z(double z) {
        this.z = z;
        return this;
    }

    public LocationBuilder yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public LocationBuilder pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public static LocationBuilder of(World world) {
        return new LocationBuilder(world);
    }

    public static LocationBuilder of(String worldName) {
        return new LocationBuilder(worldName);
    }

    public static LocationBuilder fromPlayer(Player player)
    {
        return new LocationBuilder(player.getWorld())
                .x(player.getLocation().getX())
                .y(player.getLocation().getY())
                .z(player.getLocation().getZ())
                .yaw(player.getLocation().getYaw())
                .pitch(player.getLocation().getPitch());
    }

    public static LocationBuilder fromBlock(Block block)
    {
        return new LocationBuilder(block.getWorld())
                .x(block.getX())
                .y(block.getY())
                .z(block.getZ());
    }

    @Override
    public Location build()
    {
        World w = Bukkit.getWorld(world);
        if (w == null) throw new IllegalStateException("World not found: " + world);
        return new Location(w, x, y, z, yaw, pitch);
    }
}
