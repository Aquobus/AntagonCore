package com.aquobus.antagoncore.modules.betterLeaves;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class BetterLeaves implements Listener {
    private final AntagonCore plugin;

    public BetterLeaves(AntagonCore plugin) {
        this.plugin = plugin;
    }

    private final List<Material> leavesTypes = Arrays.asList(
            Material.ACACIA_LEAVES,
            Material.AZALEA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.CHERRY_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.FLOWERING_AZALEA_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.MANGROVE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.isBetterLeavesEnabled) {
            return;
        }

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

//        if (leavesTypes.contains(from.getBlock().getType())) {
//            if (leavesTypes.contains(to.getBlock().getType())) {
//                if (to.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
//                    to.setY(to.getY() - 0.1);
//                    player.teleport(to);
//                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 0, false, false, false));
//                }
//            }
//        }
//        else if (leavesTypes.contains(from.getBlock().getType())) {
//            if (to.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
//                to.setY(to.getY() - 0.1);
//                player.teleport(to);
//                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 1, false, false, false));
//            }
//        }

        if (leavesTypes.contains(from.getBlock().getType()) || leavesTypes.contains(to.getBlock().getType())) {
            Utils.msg(player,"вы на листве");
            boolean canPassThrough = !to.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR);

            if (canPassThrough) {
                to.setY(to.getY() - 0.1);
                player.teleport(to);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1, 1, false, false, false));
            }
        }
    }
}
