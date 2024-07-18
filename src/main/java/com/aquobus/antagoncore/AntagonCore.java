package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.discord_bot.DiscordCommandEvents;
import com.aquobus.antagoncore.discord_bot.DiscordCommands;
import com.aquobus.antagoncore.discord_bot.DiscordReadyEvents;
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

    public static AntagonCore getPlugin() {
        return plugin;
    }

    public void reload() {
        reloadConfig();
        getConfig();
        this.config = getConfig();
    }

    @Override
    public void onLoad() {
        DiscordSRV.api.subscribe(new DiscordReadyEvents());
        DiscordSRV.api.subscribe(new DiscordCommandEvents());
        DiscordSRV.api.subscribe(new DiscordCommands());
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
        DiscordSRV.api.subscribe(new DiscordReadyEvents());
        DiscordSRV.api.subscribe(new DiscordCommandEvents());
        DiscordSRV.api.subscribe(new DiscordCommands());
        // Commands register
        Objects.requireNonNull(getServer().getPluginCommand("antagoncore")).setExecutor(new ACore(this));
        // TabCompleter register
        Objects.requireNonNull(getServer().getPluginCommand("antagoncore")).setTabCompleter(new CommandCompleter());

        getServer().getLogger().info("AntagonCore успешно был включен");
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }
}
