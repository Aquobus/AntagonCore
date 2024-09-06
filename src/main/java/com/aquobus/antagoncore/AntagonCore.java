package com.aquobus.antagoncore;

import com.aquobus.antagoncore.commands.ACore;
import com.aquobus.antagoncore.commands.CommandCompleter;
import com.aquobus.antagoncore.modules.antiElytra.ElytraListener;
import com.aquobus.antagoncore.modules.betterLeaves.BetterLeaves;
import com.aquobus.antagoncore.modules.discord_bot.DiscordCommandEvents;
import com.aquobus.antagoncore.modules.discord_bot.DiscordCommands;
import com.aquobus.antagoncore.modules.discord_bot.DiscordReadyEvents;
import com.aquobus.antagoncore.modules.fastDirtPath.FastDirtPath;
import com.aquobus.antagoncore.modules.fastMinecarts.FastMinecarts;
import com.aquobus.antagoncore.modules.kingdoms.clanlimiter.events.ClanLimiterListener;
import com.aquobus.antagoncore.modules.kingdoms.discordsrv_hook.DiscordsrvListener;
import com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.handlers.OutpostListener;
import com.aquobus.antagoncore.modules.lightningToGlass.LightningToGlass;
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
import org.kingdoms.constants.metadata.StandardKingdomMetadataHandler;
import org.kingdoms.constants.namespace.Namespace;

import java.util.ArrayList;
import java.util.Objects;

public final class AntagonCore extends JavaPlugin {
    public static AntagonCore plugin;
    public static KingdomMetadataHandler shield_time;
    public static KingdomMetadataHandler kHandler;
    public static KingdomMetadataHandler outpost_id;
    public static RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

    public boolean isAntiElytraEnabled;
    public boolean isBetterLeavesEnabled;
    public boolean isDiscordsrvAddonEnabled;
    public boolean isFastMinecartsEnabled;
    public boolean isFastDirtPathEnabled;
    public boolean isKingdomsClanLimiterEnabled;
    public boolean isKingdomsDiscordsrvAddonEnabled;
    public boolean isKingdomsColoniesEnabled;
    public boolean isResourcepackSafeLoadEnabled;
    public boolean isLuckPermsCheckerEnabled;
    public boolean isLightningToGlassEnabled;
    public boolean isVillagerTransportationEnabled;
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
        this.isBetterLeavesEnabled              = config.getBoolean("modules.betterLeaves");
        this.isDiscordsrvAddonEnabled           = config.getBoolean("modules.discordsrv");
        this.isFastMinecartsEnabled             = config.getBoolean("modules.fastMinecarts");
        this.isFastDirtPathEnabled              = config.getBoolean("modules.fastDirtPath");
        this.isKingdomsClanLimiterEnabled       = config.getBoolean("modules.clanLimiter");
        this.isKingdomsDiscordsrvAddonEnabled   = config.getBoolean("modules.kingdomsDiscordsrv");
        this.isKingdomsColoniesEnabled          = config.getBoolean("modules.outposts");
        this.isResourcepackSafeLoadEnabled      = config.getBoolean("modules.resourcePackSafeLoad");
        this.isLuckPermsCheckerEnabled          = config.getBoolean("modules.luckPerms");
        this.isLightningToGlassEnabled          = config.getBoolean("modules.lightningToGlass");
        this.isVillagerTransportationEnabled    = config.getBoolean("modules.villagerTransportation");
    }

    public void reload() {
        this.config = getConfig();

        // Создаём новый файл конфига если его версия устарела
        if (config.getInt("version", 0) < 2) {
            getLogger().info("Обновление конфигурационного файла до стандартной версии.");
            saveDefaultConfig(); // сохраняет стандартный конфиг и перезаписывает
        }
        reloadConfig(); // заново загружает конфиг
        getModules();
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
        packLoaded = new ArrayList<>();

        // Plugin startup logic
        reload();
        getModules();

        if (provider != null) {
            this.api = provider.getProvider();
        }

        // Events register
        getServer().getPluginManager().registerEvents(new BetterLeaves(this), this);
        getServer().getPluginManager().registerEvents(new FastDirtPath(this), this);
        getServer().getPluginManager().registerEvents(new ElytraListener(this), this);
        getServer().getPluginManager().registerEvents(new OutpostListener(this), this);
        getServer().getPluginManager().registerEvents(new ClanLimiterListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRightsListener(this), this);
        getServer().getPluginManager().registerEvents(new LoadListener(this), this);
        getServer().getPluginManager().registerEvents(new DiscordsrvListener(this), this);
        getServer().getPluginManager().registerEvents(new LightningToGlass(this), this);

        new FastMinecarts(this);

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
    }
}
