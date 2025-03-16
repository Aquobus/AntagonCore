package com.aquobus.antagoncore.player;

import com.aquobus.antagoncore.AntagonCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.kingdoms.constants.player.KingdomPlayer;

public class PlayerInteractionPlaceholders extends PlaceholderExpansion {
    
    private final AntagonCore plugin;
    
    public PlayerInteractionPlaceholders(AntagonCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "antagoninteract";
    }
    
    @Override
    public String getAuthor() {
        return "AntagonCore";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        
        Player target = PlayerInteractionManager.getInteractionTarget(player);
        if (target == null) {
            return "No target";
        }
        
        switch (identifier) {
            case "target_name":
                return target.getName();
            case "target_displayname":
                return target.getDisplayName();
            case "target_ping":
                try {
                    // This is a reflection-based approach, might need adjustment based on server version
                    Object entityPlayer = target.getClass().getMethod("getHandle").invoke(target);
                    return String.valueOf(entityPlayer.getClass().getField("ping").getInt(entityPlayer));
                } catch (Exception e) {
                    return "N/A";
                }
            case "target_playtime":
                // This assumes you have a way to track playtime
                // You might need to implement this based on your server's stats system
                return "Unknown";
            case "target_kingdom":
                // Check if Kingdoms plugin is available
                if (Bukkit.getPluginManager().isPluginEnabled("Kingdoms")) {
                    KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(target);
                    if (kp != null && kp.getKingdom() != null) {
                        return kp.getKingdom().getName();
                    }
                    return "No Kingdom";
                }
                return "Kingdoms not available";
            default:
                return null;
        }
    }
}
