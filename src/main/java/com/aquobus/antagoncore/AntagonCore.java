package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.discord_bot.slash_commands;
import com.aquobus.antagoncore.kingdoms.clanlimiter.events.ClanLimiterListener;
import com.aquobus.antagoncore.kingdoms.discordsrv_hook.DiscordsrvListener;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.handlers.ElytraListener;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.handlers.OutpostListener;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.kingdoms.constants.metadata.KingdomMetadataHandler;

import java.util.Objects;

public final class AntagonCore extends JavaPlugin {
    public static AntagonCore plugin;
    public static KingdomMetadataHandler shield_time;
    
    public FileConfiguration config = getConfig();
    private final slash_commands slash_commands = new slash_commands(this);

    public static AntagonCore getPlugin() {
        return plugin;
    }

    public void reload() {
        reloadConfig();
        getConfig();
        this.config = getConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Plugin startup logic
        saveDefaultConfig();
        // Events register
        getServer().getPluginManager().registerEvents(new ElytraListener(), this);
        getServer().getPluginManager().registerEvents(new OutpostListener(), this);
        
        getServer().getPluginManager().registerEvents(new ClanLimiterListener(this), this);
        getServer().getPluginManager().registerEvents(new DiscordsrvListener(this), this);
        getServer().getPluginManager().registerEvents(new slash_commands(this), this);
        DiscordSRV.api.subscribe(slash_commands);

        // Commands register
        Objects.requireNonNull(getServer().getPluginCommand("antagoncore")).setExecutor(new ACore(this));

        // TabCompleter register
        Objects.requireNonNull(getServer().getPluginCommand("antagoncore")).setTabCompleter(new CommandCompleter());

        getServer().getLogger().info("AntagonCore успешно был включен");
    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(slash_commands);
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
