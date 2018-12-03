package net.arcation.cellblock.impl;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import net.arcation.cellblock.api.CellItemManager;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@Singleton
public class CellItemManagerImpl implements CellItemManager {

    private static final String CELL_ITEM_NAME = "Hell is empty and all the devils are here";
    private static final String WHITESPACE = "";
    private final Set<Material> bindableItems = new HashSet<>();

    @Override
    public ItemStack bindItemToLocation(final ItemStack itemStack, final Location location) {
        if (!canItemBeBound(itemStack))
            return itemStack;
        final ItemStack bound = new ItemStack(itemStack.getType(), 1);
        final ItemMeta meta = bound.getItemMeta();
        meta.setLore(getLore(location));
        meta.setDisplayName(CELL_ITEM_NAME);
        meta.addEnchant(Enchantment.DURABILITY, 2, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bound.setItemMeta(meta);
        return bound;
    }

    @Override
    public Location getBoundLocation(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null)
            return null;
        return getLocation(itemStack.getItemMeta().getLore());
    }

    @Override
    public List<ItemStack> getBoundItems(final Inventory inventory) {
        return null;
    }

    private Location getLocation(final List<String> lore) {
        if (lore == null || lore.size() != 2)
            return null;
        final World world = Bukkit.getWorld(lore.get(0));
        if (world == null)
            return null;
        final String locString = lore.get(1);
        final String[] parts = locString.split(WHITESPACE);
        if (parts.length != 3)
            return null;
        try {
            return new Location(world, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            //TODO---Log
            //log.error("Could not load a location from lore from lore: " + String.join(" ", lore));
            return null;
        }
    }

    private List<String> getLore(final Location location) {
        return new ImmutableList.Builder<String>()
                .add(location.getWorld().getName())
                .add(location.getBlockX() + WHITESPACE + location.getBlockY() + WHITESPACE + location.getBlockZ())
                .build();
    }

    private boolean canItemBeBound(final ItemStack item) {
        return item != null && (bindableItems.isEmpty() || bindableItems.contains(item.getType()))
                && (item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty());
    }
}
