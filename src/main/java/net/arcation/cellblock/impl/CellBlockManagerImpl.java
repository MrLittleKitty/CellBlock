package net.arcation.cellblock.impl;

import net.arcation.cellblock.api.CellBlockManager;
import net.arcation.cellblock.api.CellItemManager;
import org.bukkit.Location;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class CellBlockManagerImpl implements CellBlockManager {
    private final Map<UUID, Location> cellBlockLocations = new HashMap<>();

    @Override
    public boolean isImprisoned(final UUID player) {
        return cellBlockLocations.containsKey(player);
    }

    @Override
    public Location getPrisonLocation(UUID player) {
        return cellBlockLocations.get(player);
    }

    @Override
    public void freePlayer(final UUID player) {
        cellBlockLocations.remove(player);
    }

    @Override
    public void imprisonPlayer(final UUID player, final Location cellLocation) {
        cellBlockLocations.put(player, cellLocation);
    }
}
