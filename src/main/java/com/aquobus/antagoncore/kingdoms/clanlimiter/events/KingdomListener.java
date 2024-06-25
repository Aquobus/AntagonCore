package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupDisband;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.Objects;

public class KingdomListener implements Listener {
    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = event.getKingdom();
        KingdomPlayer king = Objects.requireNonNull(event.getKingdom()).getKing();
        Player player = king.getPlayer();

        assert player != null;
        player.sendMessage("Ваше величество, поздравляю с основанием королевства! Но учтите, что если в королевстве не будет минимум 3 человека через 12 часов после его создания, то оно будет уничтожено");

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(AntagonCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                assert kingdom != null;
                if (kingdom.getKingdomPlayers().size() < 3) {
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                    if (player.isOnline()) {
                        player.sendMessage("Ваше королевство было уничтожено по причине недостаточного количества людей");
                    }
                }
            }
        }, 200L);
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        Kingdom kingdom = event.getKingdom();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(AntagonCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (kingdom.getKingdomPlayers().size() < AntagonCore.getPlugin().disbandPlayerMinimum) {
                    for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                        Player player = kingdomPlayer.getPlayer();
                        if (player != null && player.isOnline()) {
                            player.sendMessage("Ваше королевство будет уничтожено из-за недостаточного количества игроков.");
                        }
                    }
                    kingdom.getGroup().disband(GroupDisband.Reason.CUSTOM);
                }
            }
        }, AntagonCore.getPlugin().disbandDelayHoursAfterLeavedPlayer * 60 * 60 * 20L); // Задержка в часах, конвертирующаяся в тики
    }
}
