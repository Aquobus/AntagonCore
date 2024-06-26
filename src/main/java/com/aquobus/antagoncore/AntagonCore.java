package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.kingdoms.clanlimiter.events.KingdomListener;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
        disbandPlayerMinimum = config.getInt("settings.disbandPlayerMinimum", 3);

        // Events register
        getServer().getLogger().info("AntagonCore успешно был включен");
        getServer().getPluginManager().registerEvents(new KingdomListener(this), this);

        // Commands register
        getServer().getPluginCommand("antagoncore").setExecutor(new ACore(this));
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
