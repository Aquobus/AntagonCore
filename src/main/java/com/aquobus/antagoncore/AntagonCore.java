package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.kingdoms.clanlimiter.events.ClanLimiterListener;
import com.aquobus.antagoncore.kingdoms.discordsrv_hook.KingdomCreateListener;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.handlers.ElytraListener;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.handlers.OutpostListener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

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
        plugin = this;

        getServer().getLogger().info("AntagonCore успешно был включен");

        // Plugin startup logic
        saveDefaultConfig();

        // Events register
        getServer().getPluginManager().registerEvents(new ElytraListener(this), this);
        getServer().getPluginManager().registerEvents(new ClanLimiterListener(this), this);
        getServer().getPluginManager().registerEvents(new OutpostListener(this), this);
        getServer().getPluginManager().registerEvents(new KingdomCreateListener(this), this);
        

        // Commands register
        Objects.requireNonNull(getServer().getPluginCommand("antagoncore")).setExecutor(new ACore(this));
        
        // TabCompleter register
        Objects.requireNonNull(getServer().getPluginCommand("antagoncore")).setTabCompleter(new CommandCompleter());
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
