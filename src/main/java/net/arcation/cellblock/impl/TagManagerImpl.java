package net.arcation.cellblock.impl;

import net.arcation.cellblock.api.TagManager;

import java.util.UUID;

public class TagManagerImpl implements TagManager {
    @Override
    public boolean isPlayerTagged(UUID player) {
        return false;
    }
}
