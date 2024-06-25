package com.aquobus.antagoncore;

import com.aquobus.antagoncore.kingdoms.clanlimiter.events.KingdomListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class AntagonCore extends JavaPlugin {

    public int disbandDelayHoursAfterLeavedPlayer;
    public int disbandDelayHours;
    public int disbandPlayerMinimum;
    public static AntagonCore plugin;

    public static AntagonCore getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new KingdomListener(), this);

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        disbandDelayHoursAfterLeavedPlayer = config.getInt("settings.disbandDelayHoursAfterLeavedPlayer", 24);
        disbandDelayHours = config.getInt("settings.disbandDelayHours", 72);
        disbandPlayerMinimum = config.getInt("settings.disbandPlayerMinimum", 3);

        getServer().getLogger().info("AntagonCore успешно был включен");
        getServer().getPluginManager().registerEvents(new KingdomListener(), this);
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
