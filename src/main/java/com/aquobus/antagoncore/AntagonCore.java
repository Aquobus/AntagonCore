package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.kingdoms.clanlimiter.events.KingdomListener;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.events.ElytraListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class AntagonCore extends JavaPlugin {
    public static AntagonCore plugin;
    public FileConfiguration config = getConfig();

    public static AntagonCore getPlugin() {
        return plugin;
    }

    public void reload() {
        reloadConfig();
        config = getConfig();
    }

    @Override
    public void onEnable() {
        getServer().getLogger().info("AntagonCore успешно был включен");

        // Plugin startup logic
        saveDefaultConfig();

        // Events register
        getServer().getPluginManager().registerEvents(new ElytraListener(this), this);
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
