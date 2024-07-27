package com.aquobus.antagoncore.modules.antiElytra;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.utils.Utils;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class ElytraListener implements Listener {
    private static final Set<Player> elytraCancelling = new HashSet<>();
    private AntagonCore plugin;

    public ElytraListener(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onElytra(EntityToggleGlideEvent event) {
        if (!plugin.isAntiElytraEnabled) {
            return;
        }

        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        if (!event.isGliding()) {
            return;
        }

        Player player = (Player) event.getEntity();

        User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player);
        if (Boolean.parseBoolean(user.getCachedData().getMetaData().getMetaValue("cancelElytra"))) {
            event.setCancelled(true);
            Utils.msg(player,"&6Ваши элитры были &cотключены");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }
        if (player.isInRain()) {
            event.setCancelled(true);
            Utils.msg(player,"&6Элитры &cне могут быть использованы&6 во время дождя");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }
    }

    @EventHandler
    public void onGlide(PlayerMoveEvent event) {
        if (!plugin.isAntiElytraEnabled) {
            return;
        }

        Player player = event.getPlayer();
        if (elytraCancelling.contains(player)) {
            return;
        }

        if (!player.isInRain() || !player.isGliding()) {
            return;
        }

        elytraCancelling.add(player);
        Bukkit.getScheduler().runTaskLater(AntagonCore.getPlugin(), () -> {
            player.sendMessage(Utils.toComponent("&6Кажется ваши элитры промокли..."));
            player.playSound(player.getLocation(), Sound.ENCHANT_THORNS_HIT, 2F, 0.8F);
            if (!player.isGliding()) {
                elytraCancelling.remove(player);
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || player.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                        elytraCancelling.remove(player);
                        this.cancel();
                        return;
                    }
                    player.setGliding(false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 25, 0, true, false));
                }
            }.runTaskTimer(AntagonCore.getPlugin(), 20, 20);
        }, 20);
    }
}
