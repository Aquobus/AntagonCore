package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import com.aquobus.antagoncore.AntagonCore;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.*;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.HashMap;
import java.util.Objects;

public class ClanLimiterListener implements Listener {
    private final AntagonCore plugin;
    private final int playerMinimum;
    private final int delayH;
    private final int delayHAfterLeave;

    public ClanLimiterListener(AntagonCore plugin) {
        this.plugin = plugin;

        playerMinimum = plugin.config.getInt("kingdomSettings.disbandPlayerMinimum", 3);
        delayH = plugin.config.getInt("kingdomSettings.disbandDelayHours", 12);
        delayHAfterLeave = plugin.config.getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 24);
    }

    private Kingdom kingdom;
    private Player player;
    private HashMap<Kingdom,Integer> TaskId = new HashMap<Kingdom,Integer>();

    private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        player = kingdom.getKing().getPlayer();
        assert player != null;

        player.sendMessage("Ваше величество, поздравляем с созданием королевства! Но учтите, что если в королевстве не наберется " + playerMinimum + " игроков, то через " + delayH + " часов королевство " + ChatColor.RED + " будет уничтожено");

        TaskId.put(kingdom, scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                assert kingdom != null;
                if (kingdom.getKingdomPlayers().size() < playerMinimum) {
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                    if (player.isOnline()) {
                        player.sendMessage("Ваше королевство было уничтожено из-за недостаточного количества игроков.");
                    }
                }
            }
        }, delayH * 3600 * 20L));
    }
    @EventHandler
    public void onKingdomDisband(KingdomDisbandEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = kingdom.getKing().getPlayer();
        assert player != null;

        if (TaskId.containsKey(event.getKingdom())) {
            scheduler.cancelTask(TaskId.get(event.getKingdom()));
        }
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (kingdom.getKingdomPlayers().size() < playerMinimum) {
                    for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                        Player player = kingdomPlayer.getPlayer();
                        if (player != null && player.isOnline()) {
                            player.sendMessage("Ваше королевство будет уничтожено из-за недостаточного количества игроков.");
                        }
                    }
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);
                }
            }
        }, delayHAfterLeave); // Задержка в часах, конвертирующаяся в тики
    }
}