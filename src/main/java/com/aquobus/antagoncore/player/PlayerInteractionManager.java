package com.aquobus.antagoncore.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInteractionManager {
    
    private static final Map<UUID, UUID> interactionTargets = new HashMap<>();
    
    public static void setInteractionTarget(Player player, Player target) {
        interactionTargets.put(player.getUniqueId(), target.getUniqueId());
    }
    
    public static Player getInteractionTarget(Player player) {
        UUID targetUUID = interactionTargets.get(player.getUniqueId());
        if (targetUUID == null) {
            return null;
        }
        return player.getServer().getPlayer(targetUUID);
    }
    
    public static void clearInteractionTarget(Player player) {
        interactionTargets.remove(player.getUniqueId());
    }
}
