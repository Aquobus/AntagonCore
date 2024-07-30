package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.modules.antiElytra.ElytraListener;
import com.aquobus.antagoncore.modules.discord_bot.DiscordCommandEvents;
import com.aquobus.antagoncore.modules.discord_bot.DiscordCommands;
import com.aquobus.antagoncore.modules.discord_bot.DiscordReadyEvents;
import com.aquobus.antagoncore.modules.fastMinecarts.FastMinecarts;
import com.aquobus.antagoncore.modules.kingdoms.discordsrv_hook.DiscordsrvListener;
import com.aquobus.antagoncore.modules.luckperms.PlayerRightsListener;
import com.aquobus.antagoncore.modules.resourcePackSafeLoad.LoadListener;
import github.scarsz.discordsrv.DiscordSRV;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kingdoms.constants.metadata.KingdomMetadataHandler;

import java.util.ArrayList;
import java.util.Objects;

public final class AntagonCore extends JavaPlugin {
    public static AntagonCore plugin;
    public static KingdomMetadataHandler shield_time;
    public static KingdomMetadataHandler kHandler;
    public static KingdomMetadataHandler outpost_id;
    public static RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

    public boolean isAntiElytraEnabled;
    public boolean isDiscordsrvAddonEnabled;
    public boolean isFastMinecartsEnabled;
    public boolean isKingdomsClanLimiterEnabled;
    public boolean isKingdomsDiscordsrvAddonEnabled;
    public boolean isKingdomsColoniesEnabled;
    public boolean isResourcepackSafeLoadEnabled;
    public boolean isLuckPermsCheckerEnabled;

    public FileConfiguration config = getConfig();
    public LuckPerms api;

    // Safe resource pack loading
    public static ArrayList<Player> packLoaded;
    private static AntagonCore instance;

    public static AntagonCore getInstance(){
        return instance;
    }

    public static AntagonCore getPlugin() {
        return plugin;
    }

    public void getModules() {
        this.isAntiElytraEnabled                = config.getBoolean("modules.antiElytra");
        this.isDiscordsrvAddonEnabled           = config.getBoolean("modules.discordsrv");
        this.isFastMinecartsEnabled             = config.getBoolean("modules.fastMinecarts");
        this.isKingdomsClanLimiterEnabled       = config.getBoolean("modules.clanLimiter");
        this.isKingdomsDiscordsrvAddonEnabled   = config.getBoolean("modules.kingdomsDiscordsrv");
        this.isKingdomsColoniesEnabled          = config.getBoolean("modules.outposts");
        this.isResourcepackSafeLoadEnabled      = config.getBoolean("modules.resourcePackSafeLoad");
        this.isLuckPermsCheckerEnabled          = config.getBoolean("modules.luckPerms");
    }

    public void reload() {
        this.config = getConfig();

        // Создаём новый файл конфига если его версия устарела
        if (config.getInt("version") > 1 || config.getInt("version") != 1) {
            saveDefaultConfig();
        }
        reloadConfig();
        getModules();
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        plugin = this;
        instance = this;
        // Plugin startup logic
        getConfig();
        if (config.getInt("version") > 1 || config.getInt("version") != 1) {
            saveDefaultConfig();
        }
        getModules();
        // Events register
        if (isAntiElytraEnabled) {
            getServer().getPluginManager().registerEvents(new ElytraListener(this), this);
        }
//        if (isKingdomsColoniesEnabled) {
//            outpost_id = new StandardKingdomMetadataHandler(new Namespace("AntagonCore", "OUTPOST_ID"));
//            kHandler = new StandardKingdomMetadataHandler(new Namespace("AntagonCore", "KHANDLER"));
//            getServer().getPluginManager().registerEvents(new OutpostListener(this), this);
//        }
//        if (isKingdomsClanLimiterEnabled) {
//            getServer().getPluginManager().registerEvents(new ClanLimiterListener(this), this);
//        }
        if (isLuckPermsCheckerEnabled){
            if (provider != null) {
                this.api = provider.getProvider();
            }
            getServer().getPluginManager().registerEvents(new PlayerRightsListener(this), this);
        }
        if (isFastMinecartsEnabled) {
            new FastMinecarts(this).loadFastMinecartsConfig();
            getServer().getPluginManager().registerEvents(new FastMinecarts(this), this);
        }
        if (isResourcepackSafeLoadEnabled) {
            packLoaded = new ArrayList<>();
            getServer().getPluginManager().registerEvents(new LoadListener(this), this);
        }
        if (isKingdomsDiscordsrvAddonEnabled) {
            getServer().getPluginManager().registerEvents(new DiscordsrvListener(this), this);
        }
        if (isDiscordsrvAddonEnabled) {
            DiscordSRV.api.subscribe(new DiscordReadyEvents());
            DiscordSRV.api.subscribe(new DiscordCommandEvents());
            DiscordSRV.api.subscribe(new DiscordCommands());
        }
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
    }
}
