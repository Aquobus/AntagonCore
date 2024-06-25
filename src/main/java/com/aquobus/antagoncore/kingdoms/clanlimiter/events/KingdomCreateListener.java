package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

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

import java.util.Objects;

public class KingdomCreateListener implements Listener {
    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = event.getKingdom();
        KingdomPlayer king = Objects.requireNonNull(event.getKingdom()).getKing();
        Player player = king.getPlayer();

        assert player != null;
        player.sendMessage("Ваше величество, поздравляю с основанием королевства! Но учтите, что если в королевстве не будет минимум 3 человека через 12 часов после его создания, то оно будет уничтожено");

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask((Plugin) this, new Runnable() {
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
}
