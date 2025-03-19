package com.aquobus.antagoncore.modules.kingdoms;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class KingdomsModule {
    
    private static Object outpost_id;
    private static Object kHandler;
    
    public static void initialize(AntagonCore plugin) {
        try {
            // Load classes using reflection
            Class<?> namespaceClass = Class.forName("org.kingdoms.constants.namespace.Namespace");
            Class<?> handlerClass = Class.forName("org.kingdoms.constants.metadata.StandardKingdomMetadataHandler");
            
            // Create namespace objects
            Object outpostNamespace = namespaceClass.getConstructor(String.class, String.class)
                .newInstance("AntagonCore", "OUTPOST_ID");
            Object kHandlerNamespace = namespaceClass.getConstructor(String.class, String.class)
                .newInstance("AntagonCore", "KHANDLER");
            
            // Create metadata handlers
            outpost_id = handlerClass.getConstructor(namespaceClass).newInstance(outpostNamespace);
            kHandler = handlerClass.getConstructor(namespaceClass).newInstance(kHandlerNamespace);
            
            // Register event listeners
            try {
                Class<?> outpostListenerClass = Class.forName("com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.handlers.OutpostListener");
                Class<?> clanLimiterListenerClass = Class.forName("com.aquobus.antagoncore.modules.kingdoms.clanlimiter.events.ClanLimiterListener");
                
                Listener outpostListener = (Listener) outpostListenerClass.getConstructor(AntagonCore.class).newInstance(plugin);
                Listener clanLimiterListener = (Listener) clanLimiterListenerClass.getConstructor(AntagonCore.class).newInstance(plugin);
                
                Bukkit.getPluginManager().registerEvents(outpostListener, plugin);
                Bukkit.getPluginManager().registerEvents(clanLimiterListener, plugin);
                
                // Register DiscordSRV listener if available
                if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
                    Class<?> discordSrvListenerClass = Class.forName("com.aquobus.antagoncore.modules.kingdoms.discordsrv_hook.DiscordsrvListener");
                    Listener discordSrvListener = (Listener) discordSrvListenerClass.getConstructor(AntagonCore.class).newInstance(plugin);
                    Bukkit.getPluginManager().registerEvents(discordSrvListener, plugin);
                }
                
                plugin.getLogger().info("Kingdoms event listeners registered successfully");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to register Kingdoms event listeners: " + e.getMessage());
            }
            
            plugin.getLogger().info("Kingdoms module initialized successfully");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize Kingdoms module: " + e.getMessage());
        }
    }
    
    public static Object getOutpostId() {
        return outpost_id;
    }
    
    public static Object getKHandler() {
        return kHandler;
    }
}
