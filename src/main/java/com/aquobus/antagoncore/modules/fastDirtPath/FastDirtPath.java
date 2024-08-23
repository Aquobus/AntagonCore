package com.aquobus.antagoncore.modules.fastDirtPath;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FastDirtPath implements Listener {
    private final AntagonCore plugin;

    public FastDirtPath(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.isFastDirtPathEnabled) {
            return;
        }
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock().getRelative(0, -1, 0);

        if (block.getType() == Material.DIRT_PATH & !player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0, false, false, false));

            player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 3);
        }
    }
}
