package com.aquobus.antagoncore;

import com.aquobus.antagoncore.kingdoms.clanlimiter.events.KingdomListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class AntagonCore extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new KingdomListener(this), this);
        getServer().getLogger().info("AntagonCore успешно был включен");
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
