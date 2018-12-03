package net.arcation.cellblock.api;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CellItemManager {

    ItemStack bindItemToLocation(final ItemStack itemStack, final Location location);

    Location getBoundLocation(final ItemStack itemStack);

    List<ItemStack> getBoundItems(final Inventory inventory);

}
