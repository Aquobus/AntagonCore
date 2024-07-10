package com.aquobus.antagoncore.kingdoms.clanlimiter.events;

import com.aquobus.antagoncore.AntagonCore;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.RoleAction;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.*;
import org.kingdoms.events.members.KingdomJoinEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;
import github.scarsz.discordsrv.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Objects;
import java.util.UUID;

public class KingdomListener implements Listener {
    private AntagonCore plugin;
    private int playerMinimum;
    private int delayH;
    private int delayHAfterLeave;
    private Role roleOnCreation;

    public KingdomListener(AntagonCore plugin) {
        this.plugin = plugin;

        playerMinimum = plugin.config.getInt("kingdomSettings.disbandPlayerMinimum", 3);
        delayH = plugin.config.getInt("kingdomSettings.disbandDelayHours", 12);
        delayHAfterLeave = plugin.config.getInt("kingdomSettings.disbandDelayHoursAfterLeavedPlayer", 24);
        roleOnCreation = DiscordUtil.getRole(plugin.config.getString("kingdomSettings.giveDiscordRoleOnKingdomCreation"));
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
        UUID kingId = player.getUniqueId();
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(kingId);
        Member discordMember = DiscordUtil.getMemberById(discordId);
        // Выдать главе клана роль
        DiscordUtil.addRoleToMember(discordMember, roleOnCreation);
        // Создать роль "Название клана"
        RoleAction clanRole = discordMember.getGuild().createRole()
                .setName(kingdom.getName())
                .setColor(Color.getColor("#445166"))
                .setMentionable(true);

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
        UUID kingId = player.getUniqueId();
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(kingId);
        Member discordMember = DiscordUtil.getMemberById(discordId);
        DiscordUtil.removeRolesFromMember(discordMember, roleOnCreation);

        if (TaskId.containsKey(event.getKingdom())) {
            scheduler.cancelTask(TaskId.get(event.getKingdom()));
        }

        // Удаление роли "Название Клана" у всех участников
        Set<UUID> members = kingdom.getMembers();
        for (UUID member : members) {
            Member eachDMember = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(member));
            DiscordUtil.removeRolesFromMember(eachDMember, roleOnCreation);
        }
    }

    @EventHandler
    public void onKingdomKingChange(KingdomKingChangeEvent event) {
        // Написать замену роли в дискорде на нового короля
    }
    @EventHandler
    public void onGroupRenameEvent(GroupRenameEvent event) {
        // Написать замену роли участников на новое название
    }

    @EventHandler
    public void onKingdomJoin(KingdomJoinEvent event) {
        // Написать добавление роли при входе в клан
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