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
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        //p.setAllowFlight(true);

        for (Player loaded : Bukkit.getOnlinePlayers()) {
            loaded.hidePlayer(AntagonCore.getInstance(), p);
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 255, true, false));

        AntagonCore.packLoaded.remove(p);
    }

    @EventHandler
    public void onPack(PlayerResourcePackStatusEvent e) {
        Player p = e.getPlayer();
        PlayerResourcePackStatusEvent.Status status = e.getStatus();
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
        if (!AntagonCore.packLoaded.contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invDrag(InventoryDragEvent e) {
        if (!AntagonCore.packLoaded.contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invSwapHands(PlayerSwapHandItemsEvent e) {
        if (!AntagonCore.packLoaded.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invPickupArrow(PlayerPickupArrowEvent e) {
        if (!AntagonCore.packLoaded.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invPickupItem(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void invOpen(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        if (!AntagonCore.packLoaded.contains(p)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!AntagonCore.packLoaded.contains(p)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void invInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!AntagonCore.packLoaded.contains(p)) {
            e.setCancelled(true);
        }
    }

    // Mobility events
    @EventHandler
    public void onDamaged(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDamageOthers(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!AntagonCore.packLoaded.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    // Entity targeting
    @EventHandler
    public void onTarget(EntityTargetEvent e) {
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
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (!AntagonCore.packLoaded.contains(p)) {
                e.setCancelled(true);
            }
        }
    }
}
