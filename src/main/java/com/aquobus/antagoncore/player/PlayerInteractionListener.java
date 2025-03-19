package com.aquobus.antagoncore.player;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PlayerInteractionListener implements Listener {
    
    private final AntagonCore plugin;
    
    public PlayerInteractionListener(AntagonCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        // Check if the feature is enabled in config
        if (!plugin.getConfig().getBoolean("modules.playerInteraction", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();
        
        if (!(target instanceof Player)) {
            return;
        }
        
        Player targetPlayer = (Player) target;
        
        if (!player.isSneaking()) {
            return;
        }
        
        if (Bukkit.getPluginManager().isPluginEnabled("GSit")) {
            if (isPlayerSitting(player)) {
                return;
            }
        }
        
        event.setCancelled(true);
        
        openPlayerInteractionMenu(player, targetPlayer);
    }
    
    private boolean isPlayerSitting(Player player) {
        return false;
    }
    
    private void openPlayerInteractionMenu(Player player, Player target) {
        PlayerInteractionManager.setInteractionTarget(player, target);
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                "dm open playerinteraction " + player.getName());
    }
}
