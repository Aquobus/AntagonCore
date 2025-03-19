package com.aquobus.antagoncore.modules.kingdoms;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.clanlimiter.events.ClanLimiterListener;
import com.aquobus.antagoncore.modules.kingdoms.discordsrv_hook.DiscordsrvListener;
import com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.handlers.OutpostListener;

import org.bukkit.Bukkit;
import org.kingdoms.constants.metadata.KingdomMetadataHandler;
import org.kingdoms.constants.metadata.StandardKingdomMetadataHandler;
import org.kingdoms.constants.namespace.Namespace;

public class KingdomsModule {
    
    private static KingdomMetadataHandler outpost_id;
    private static KingdomMetadataHandler kHandler;
    
    public static void initialize(AntagonCore plugin) {
        outpost_id = new StandardKingdomMetadataHandler(new Namespace("AntagonCore", "OUTPOST_ID"));
        kHandler = new StandardKingdomMetadataHandler(new Namespace("AntagonCore", "KHANDLER"));
        
        // Register Kingdoms-dependent events
        Bukkit.getPluginManager().registerEvents(new OutpostListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new ClanLimiterListener(plugin), plugin);
        
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            Bukkit.getPluginManager().registerEvents(new DiscordsrvListener(plugin), plugin);
        }
    }
    
    public static KingdomMetadataHandler getOutpostId() {
        return outpost_id;
    }
    
    public static KingdomMetadataHandler getKHandler() {
        return kHandler;
    }
}
