package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
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

    public void reload() {
        reloadConfig();
    }

    @Override
    public void onEnable() {
        

        // Plugin startup logic
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        disbandDelayHoursAfterLeavedPlayer = config.getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 24);
        disbandDelayHours = config.getInt("kingdomSettings.disbandDelayHours", 72);
        disbandPlayerMinimum = config.getInt("kingdomSettings.disbandPlayerMinimum", 3);

        // Events register
        getServer().getLogger().info("AntagonCore успешно был включен");
        getServer().getPluginManager().registerEvents(new KingdomListener(this), this);

        // Commands register
        getServer().getPluginCommand("antagoncore").setExecutor(new ACore(this));
        
        // TabCompleter register
        getServer().getPluginCommand("antagoncore").setTabCompleter(new CommandCompleter());
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
