package de.glowman554.onlyup;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record Coordinate(int x, int y, int z) {
    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
}
