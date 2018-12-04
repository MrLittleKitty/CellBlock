package net.arcation.cellblock;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.arcation.cellblock.api.CellBlockManager;
import net.arcation.cellblock.api.CellItemManager;
import net.arcation.cellblock.listener.DamageListener;
import net.arcation.cellblock.listener.RespawnListener;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CellBlockPlugin extends JavaPlugin {

    public void onEnable() {
        final Injector injector = Guice.createInjector(new CellBlockModule(this));

        this.getServer().getPluginManager().registerEvents(injector.getInstance(DamageListener.class), this);

        System.out.println("Enabled FUCK1");
    }


    @Override
    public void onDisable() {
        System.out.println("Disabled CellBlockPlugin");
    }

}
