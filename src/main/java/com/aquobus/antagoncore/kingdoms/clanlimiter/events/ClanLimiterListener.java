package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupDisband;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.general.KingdomDisbandEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.Objects;

public class ClanLimiterListener implements Listener {
    private final int playerMinimum;
    private final int delayH;
    private final int delayHAfterLeave;

    public ClanLimiterListener(AntagonCore plugin) {
        playerMinimum = plugin.config.getInt("kingdomSettings.disbandPlayerMinimum", 3);
        delayH = plugin.config.getInt("kingdomSettings.disbandDelayHours", 12);
        delayHAfterLeave = plugin.config.getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 24);
    }

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = kingdom.getKing().getPlayer();
        assert player != null;

        player.sendMessage(String.format("Ваше величество, поздравляем с созданием королевства!\nНо учтите, что если в королевстве не наберется %s игроков,\n то через %s часов королевство &4будет уничтожено&r.", playerMinimum, delayHAfterLeave));
        /*
        TaskId.put(kingdom, scheduler.scheduleSyncDelayedTask(plugin, () -> {
            if (kingdom.getKingdomPlayers().size() < playerMinimum) {
                Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                if (player.isOnline()) {
                    player.sendMessage("Ваше королевство было уничтожено из-за недостаточного количества игроков.");
                }
            }
        }, delayH * 3600 * 20L));
        */
        Utils.scheduleAsync(delayH, () ->{
            if (kingdom.getKingdomPlayers().size() < playerMinimum & !kingdom.isPermanent()) {
                Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);

                if (player.isOnline()) {
                    player.sendMessage("&4Ваше королевство было уничтожено из-за недостаточного количества игроков.");
                }
            }
        });
    }
    @EventHandler
    public void onKingdomDisband(KingdomDisbandEvent event) {
//        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
//        Player player = kingdom.getKing().getPlayer();
//        assert player != null;
//         Я переделал на использование асинхронной задачи т.е отмена задачи не имеет смысла и не экономит ресурсов
//        if (TaskId.containsKey(event.getKingdom())) {
//            scheduler.cancelTask(TaskId.get(event.getKingdom()));
//        }
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        Kingdom kingdom = event.getKingdom();
        Utils.scheduleAsync(72000*delayHAfterLeave, () -> {
            if (kingdom.getKingdomPlayers().size() < playerMinimum) {
                for (KingdomPlayer kingdomPlayer : kingdom.getKingdomPlayers()) {
                    Player player = kingdomPlayer.getPlayer();
                    if (player != null && player.isOnline()) {
                        player.sendMessage(
                                String.format("Ваше королевство &4будет уничтожено&r через &6%s&r часов из-за недостаточного количества игроков.",delayHAfterLeave)
                        );
                    }
                }
                Objects.requireNonNull(kingdom.getGroup()).disband(GroupDisband.Reason.CUSTOM);
            }
        });
    }
}