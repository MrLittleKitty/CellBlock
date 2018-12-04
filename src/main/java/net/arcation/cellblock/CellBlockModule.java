package net.arcation.cellblock;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import net.arcation.cellblock.api.CellBlockManager;
import net.arcation.cellblock.api.CellItemManager;
import net.arcation.cellblock.api.Clock;
import net.arcation.cellblock.api.TagManager;
import net.arcation.cellblock.impl.CellBlockManagerImpl;
import net.arcation.cellblock.impl.CellItemManagerImpl;
import net.arcation.cellblock.impl.ClockImpl;
import net.arcation.cellblock.impl.TagManagerImpl;

public class CellBlockModule extends AbstractModule {

    private final CellBlockPlugin plugin;

    public CellBlockModule(final CellBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {

        bind(CellBlockPlugin.class).toInstance(plugin);

        bind(CellItemManager.class).to(CellItemManagerImpl.class).in(Singleton.class);
        bind(CellBlockManager.class).to(CellBlockManagerImpl.class).in(Singleton.class);
        bind(Clock.class).to(ClockImpl.class).in(Singleton.class);
        bind(TagManager.class).to(TagManagerImpl.class).in(Singleton.class);
    }
}
