package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupDisband;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.general.KingdomDisbandEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class KingdomListener implements Listener {
    private AntagonCore plugin;

    private Kingdom kingdom;
    private KingdomPlayer king;
    private Player player;
    private int taskId;

    private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    
    public KingdomListener(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {

        kingdom = event.getKingdom();
        king = Objects.requireNonNull(event.getKingdom()).getKing();
        player = king.getPlayer();

        assert player != null;
        player.sendMessage("Ваше величество, поздравляю с основанием королевства! Но учтите, что если в королевстве не будет минимум 3 человека через 12 часов после его создания, то оно будет уничтожено");
        taskId = scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                assert kingdom != null;
                if (kingdom.getKingdomPlayers().size() < plugin.config.getInt("kingdomSettings.disbandPlayerMinimum", 3)) {
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                    if (player.isOnline()) {
                        player.sendMessage("Ваше королевство было уничтожено из-за недостаточного количества игроков.");
                    }
                }
            }
        }, plugin.config.getInt("kingdomSettings.disbandDelayHours", 12) * 3600 * 20L);
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        Kingdom kingdom = event.getKingdom();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (kingdom.getKingdomPlayers().size() < plugin.config.getInt("kingdomSettings.disbandPlayerMinimum", 3)) {
                    for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                        Player player = kingdomPlayer.getPlayer();
                        if (player != null && player.isOnline()) {
                            player.sendMessage("Ваше королевство будет уничтожено из-за недостаточного количества игроков.");
                        }
                    }
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);
                }
            }
        }, plugin.config.getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 24)); // Задержка в часах, конвертирующаяся в тики
    }

    @EventHandler
    public void onKingdomDisband(KingdomDisbandEvent event) {
        if (event.getKingdom() == kingdom) {
            scheduler.cancelTask(taskId);
        }
    }
}