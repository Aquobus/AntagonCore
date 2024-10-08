package com.aquobus.antagoncore.modules.resourcePackSafeLoad;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LoadListener implements Listener {
    private AntagonCore plugin;

    public LoadListener(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        Player p = e.getPlayer();

        //p.setAllowFlight(true);

        for (Player loaded : Bukkit.getOnlinePlayers()) {
            loaded.hidePlayer(AntagonCore.getInstance(), p);
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 255, true, false));

        AntagonCore.packLoaded.remove(p);
    }

    @EventHandler
    public void onPack(PlayerResourcePackStatusEvent event) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        Player p = event.getPlayer();
        PlayerResourcePackStatusEvent.Status status = event.getStatus();
        PlayerResourcePackStatusEvent.Status SUCCESSFULLY_LOADED = PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED;
        PlayerResourcePackStatusEvent.Status DECLINED = PlayerResourcePackStatusEvent.Status.DECLINED;
        PlayerResourcePackStatusEvent.Status FAILED_DOWNLOAD = PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD;
        if (status.equals(SUCCESSFULLY_LOADED) || status.equals(DECLINED) || status.equals(FAILED_DOWNLOAD)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AntagonCore.getInstance(), () -> {

                AntagonCore.packLoaded.add(p);

                p.removePotionEffect(PotionEffectType.BLINDNESS);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.showPlayer(AntagonCore.getInstance(), p);
                }

                //if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                //    p.setAllowFlight(false);
                //}
            }, 10);
        }
    }

    // Inventory Events
    @EventHandler
    public void invClick(InventoryClickEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (!AntagonCore.packLoaded.contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invDrag(InventoryDragEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (!AntagonCore.packLoaded.contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invSwapHands(PlayerSwapHandItemsEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (!AntagonCore.packLoaded.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void invPickupArrow(PlayerPickupArrowEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (!AntagonCore.packLoaded.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invPickupItem(EntityPickupItemEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void invOpen(InventoryOpenEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        Player p = (Player) e.getPlayer();
        if (!AntagonCore.packLoaded.contains(p)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invDropItem(PlayerDropItemEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        Player p = e.getPlayer();
        if (!AntagonCore.packLoaded.contains(p)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invInteract(PlayerInteractEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        Player p = e.getPlayer();
        if (!AntagonCore.packLoaded.contains(p)) {
            e.setCancelled(true);
        }
    }

    // Mobility events
    @EventHandler
    public void onDamaged(EntityDamageEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDamageOthers(EntityDamageByEntityEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (!AntagonCore.packLoaded.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    // Entity targeting
    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (e.getTarget() instanceof Player) {
            Player target = (Player) e.getTarget();

            if (!AntagonCore.packLoaded.contains(target)) {
                e.setTarget(null);
            }
        }
    }

    // Health regen
    @EventHandler
    public void onRegen(EntityRegainHealthEvent e) {
        if (!plugin.isResourcepackSafeLoadEnabled) {
            return;
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
}
