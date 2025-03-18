package com.aquobus.antagoncore.modules.fastDirtPath;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

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
        Block block = player.getLocation().getBlock().getRelative(0, 0, 0);
        if (block.getType() == Material.DIRT_PATH) {
            if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
                return;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false, false));
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(200, 200, 200), 1.5F);
            player.spawnParticle(Particle.REDSTONE, player.getLocation(), 3, dustOptions);
            if (Objects.requireNonNull(player.getPotionEffect(PotionEffectType.SPEED)).getDuration() < 16) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false, false));
                player.spawnParticle(Particle.REDSTONE, player.getLocation(), 3, dustOptions);
            }
        }
    }
}
