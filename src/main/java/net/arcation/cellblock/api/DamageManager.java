package net.arcation.cellblock.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface DamageManager {

    void addDamage(final UUID player, final UUID damager, final double amount);

    List<Player> getOrderedDamagers(final UUID player);

    void clearDamage(final UUID player);
}
