package net.arcation.cellblock.api;

import java.util.List;
import java.util.UUID;

public interface DamageManager {

    void addDamage(final UUID player, final UUID damager, final double amount);

    List<UUID> getOrderedDamanger(final UUID player);

}
