package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
    // Объявляем и берём переменные из config.yml
    private int hours =  3600 * 20L
    public int disbandDelayHours = config.getInt("kingdomSettings.disbandDelayHours", 24) * hours;
    public int disbandDelayHoursAfterLeavedPlayer = config.getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 72) * hours;
    public int disbandPlayerMinimum = config.getInt("settings.disbandPlayerMinimum", 3);

    public BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {

        Kingdom kingdom = event.getKingdom();
        KingdomPlayer king = Objects.requireNonNull(event.getKingdom()).getKing();
        Player player = king.getPlayer();

        assert player != null;
        player.sendMessage("Ваше величество, поздравляю с основанием королевства! Но учтите, что если в королевстве не будет минимум 3 человека через 12 часов после его создания, то оно будет уничтожено");
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                assert kingdom != null;
                if (kingdom.getKingdomPlayers().size() < AntagonCore.getPlugin().disbandPlayerMinimum) {
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                    if (player.isOnline()) {
                        player.sendMessage("Ваше королевство было уничтожено из-за недостаточного количества игроков.");
                    }
                }
            }
        }, disbandDelayHours);
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        Kingdom kingdom = event.getKingdom();
        scheduler.scheduleSyncDelayedTask(plugin, run() {
                if (kingdom.getKingdomPlayers().size() < AntagonCore.getPlugin().disbandPlayerMinimum) {
                    for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                        Player player = kingdomPlayer.getPlayer();
                        if (player != null && player.isOnline()) {
                            player.sendMessage("Ваше королевство будет уничтожено из-за недостаточного количества игроков.");
                        }
                    }
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);
                }
            }
        }, disbandDelayHoursAfterLeavedPlayer); // Задержка в часах, конвертирующаяся в тики
    }
}