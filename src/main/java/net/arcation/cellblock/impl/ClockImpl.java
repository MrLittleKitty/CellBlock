package net.arcation.cellblock.impl;

import net.arcation.cellblock.api.Clock;

public class ClockImpl implements Clock {
    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
