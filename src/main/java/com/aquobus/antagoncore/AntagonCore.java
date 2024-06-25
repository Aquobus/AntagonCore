package com.aquobus.antagoncore;

import com.aquobus.antagoncore.kingdoms.clanlimiter.events.KingdomCreateListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntagonCore extends JavaPlugin {
    public static AntagonCore plugin;

    public static AntagonCore getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new KingdomCreateListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
