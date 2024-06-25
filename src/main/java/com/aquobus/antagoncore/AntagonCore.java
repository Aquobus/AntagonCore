package com.aquobus.antagoncore;

import com.aquobus.antagoncore.kingdoms.clanlimiter.events.KingdomCreateListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupDisband;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.general.KingdomDisbandEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.Objects;

public final class AntagonCore extends JavaPlugin implements Listener {
    public static AntagonCore plugin;

    public static AntagonCore getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {

        private int disbandDelayHoursAfterLeavedPlayer;
        private int disbandDelayHours;
        private int disbandPlayerMinimum;
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new KingdomCreateListener(), this);

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        disbandDelayHoursAfterLeavedPlayer = config.getInt("settings.disbandDelayHoursAfterLeavedPlayer", 24);
        disbandDelayHours = config.getInt("settings.disbandDelayHours", 72);
        disbandPlayerMinimum = config.getInt("settings.disbandPlayerMinimum", 3);

        getServer().getLogger().info("AntagonCore был включен");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("AntagonCore был отключен");
    }

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
                if (kingdom.getKingdomPlayers().size() < disbandPlayerMinimum) {
                    Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                    if (player.isOnline()) {
                        player.sendMessage("Ваше королевство было уничтожено по причине недостаточного количества людей");
                    }
                }
            }
        }, disbandDelayHours * 60 * 60 * 20L);
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        Kingdom kingdom = event.getKingdom();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask((Plugin) this, new Runnable() {
            @Override
            public void run() {
                if (kingdom.getKingdomPlayers().size() < disbandPlayerMinimum) {
                    for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                        Player player = kingdomPlayer.getPlayer();
                        if (player != null && player.isOnline()) {
                            player.sendMessage("Ваше королевство будет удалено из-за недостаточного количества игроков.");
                        }
                    }
                    kingdom.getGroup().disband(GroupDisband.Reason.CUSTOM);
                }
            }
        }, disbandDelayHoursAfterLeavedPlayer * 60 * 60 * 20L); // Задержка в часах, конвертирующаяся в тики
    }
}
