package net.arcation.cellblock.impl;

import net.arcation.cellblock.api.DamageManager;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class DamageManagerImpl implements DamageManager {

    @Override
    public void addDamage(UUID player, UUID damager, double amount) {

    }

    @Override
    public List<UUID> getOrderedDamanger(UUID player) {
        return null;
    }
}
