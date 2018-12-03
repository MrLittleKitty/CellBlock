package net.arcation.cellblock.api;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface CellBlockManager {

    boolean isImprisoned(final UUID player);

    Location getPrisonLocation(final UUID player);

    void freePlayer(final UUID player);

    void imprisonPlayer(final UUID player, final Location cellLocation);
}
