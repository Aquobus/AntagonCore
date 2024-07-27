package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.discord_bot.DiscordCommandEvents;
import com.aquobus.antagoncore.discord_bot.DiscordCommands;
import com.aquobus.antagoncore.discord_bot.DiscordReadyEvents;
import com.aquobus.antagoncore.kingdoms.clanlimiter.events.ClanLimiterListener;
import com.aquobus.antagoncore.kingdoms.discordsrv_hook.DiscordsrvListener;
import com.aquobus.antagoncore.modules.antiElytra.ElytraListener;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.handlers.OutpostListener;
import com.aquobus.antagoncore.modules.fastMinecarts.FastMinecarts;
import com.aquobus.antagoncore.modules.resourcePackSafeLoad.LoadListener;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kingdoms.constants.metadata.KingdomMetadataHandler;
import org.kingdoms.constants.metadata.StandardKingdomMetadataHandler;
import org.kingdoms.constants.namespace.Namespace;

import java.util.ArrayList;
import java.util.Objects;

public final class AntagonCore extends JavaPlugin {
    public static AntagonCore plugin;
    public static KingdomMetadataHandler shield_time;
    public static KingdomMetadataHandler kHandler;
    public static KingdomMetadataHandler outpost_id;

    public FileConfiguration config = getConfig();

    // Safe resource pack loading
    public static ArrayList<Player> packLoaded;
    private static AntagonCore instance;
    public static AntagonCore getInstance(){
        return instance;
    }

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
    }

    @Override
    public void onEnable() {
        plugin = this;
        instance = this;
        outpost_id = new StandardKingdomMetadataHandler(new Namespace("AntagonCore", "OUTPOST_ID"));
        kHandler = new StandardKingdomMetadataHandler(new Namespace("AntagonCore", "KHANDLER"));
        // Plugin startup logic
        saveDefaultConfig();
        getConfig();
        // Events register
        if (config.getBoolean("modules.antiElytra")) {
            getServer().getPluginManager().registerEvents(new ElytraListener(), this);
        }
        if (config.getBoolean("modules.fastMinecarts")) {
            new FastMinecarts(this).loadFastMinecartsConfig();
            getServer().getPluginManager().registerEvents(new FastMinecarts(this), this);
        }
        if (config.getBoolean("modules.resourcePackSafeLoad")) {
            packLoaded = new ArrayList<>();
            getServer().getPluginManager().registerEvents(new LoadListener(), this);
        }
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

        getLogger().info("AntagonCore успешно был включен");
        //getServer().getLogger().info("AntagonCore успешно был включен");
    }
    @Override
    public void onDisable() {
        getLogger().info("AntagonCore был отключен");
        //getServer().getLogger().info("AntagonCore был отключен");
        DiscordSRV.api.unsubscribe(new DiscordReadyEvents());
        DiscordSRV.api.unsubscribe(new DiscordCommandEvents());
        DiscordSRV.api.unsubscribe(new DiscordCommands());
    }
}
