package com.aquobus.antagoncore.utils;

import org.bukkit.Bukkit;

public class KingdomsUtil {
    private static boolean kingdomsAvailable = false;
    private static Object outpost_id = null;
    private static Object kHandler = null;
    
    static {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("Kingdoms")) {
                Class<?> namespaceClass = Class.forName("org.kingdoms.constants.namespace.Namespace");
                Class<?> handlerClass = Class.forName("org.kingdoms.constants.metadata.StandardKingdomMetadataHandler");
                
                Object namespace1 = namespaceClass.getConstructor(String.class, String.class)
                    .newInstance("AntagonCore", "OUTPOST_ID");
                Object namespace2 = namespaceClass.getConstructor(String.class, String.class)
                    .newInstance("AntagonCore", "KHANDLER");
                
                outpost_id = handlerClass.getConstructor(namespaceClass).newInstance(namespace1);
                kHandler = handlerClass.getConstructor(namespaceClass).newInstance(namespace2);
                
                kingdomsAvailable = true;
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to initialize Kingdoms API: " + e.getMessage());
            kingdomsAvailable = false;
        }
    }
    
    public static boolean isKingdomsAvailable() {
        return kingdomsAvailable;
    }
    
    public static Object getOutpostId() {
        return outpost_id;
    }
    
    public static Object getKHandler() {
        return kHandler;
    }
}
