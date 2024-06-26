package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupDisband;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.Objects;

public class KingdomListener implements Listener {
    private final JavaPlugin plugin;

    public KingdomListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        // Объявляем переменные
        final int disbandDelayHours = plugin.getConfig().getInt("kingdomSettings.disbandDelayHours", 24);
        final int disbandPlayerMinimum = plugin.getConfig().getInt("kingdomSettings.disbandPlayerMinimum", 3);
        Kingdom kingdom = event.getKingdom();
        KingdomPlayer king = Objects.requireNonNull(event.getKingdom()).getKing();
        Player player = king.getPlayer();

        assert player != null;
        player.sendMessage("Ваше величество, поздравляю с основанием королевства! Но учтите, что если в королевстве не будет минимум 3 человека через 12 часов после его создания, то оно будет уничтожено");
        // Запуск отложенной задачи
        scheduler.scheduleSyncDelayedTask(plugin, () -> {
            assert kingdom != null;
            if (kingdom.getKingdomPlayers().size() < disbandPlayerMinimum) {
                Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                if (player.isOnline()) {
                    player.sendMessage("Ваше королевство было уничтожено из-за недостаточного количества игроков.");
                }
            }
        }, disbandDelayHours * 72000L);
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        // Объявляем переменные
        final int disbandDelayHoursAfterLeavedPlayer = plugin.getConfig().getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 72);
        final int disbandPlayerMinimum = plugin.getConfig().getInt("kingdomSettings.disbandPlayerMinimum", 3);
        Kingdom kingdom = event.getKingdom();

        // Запуск отложенной задачи
        scheduler.scheduleSyncDelayedTask(plugin, () -> {
            if (kingdom.getKingdomPlayers().size() < disbandPlayerMinimum) {
                Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);
                for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                    Player player = kingdomPlayer.getPlayer();
                    if (player != null && player.isOnline()) {
                        player.sendMessage("Ваше королевство будет уничтожено из-за недостаточного количества игроков.");
                    }
                }
            }
        }, disbandDelayHoursAfterLeavedPlayer * 72000L);
    }
}