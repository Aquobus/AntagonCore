package com.aquobus.antagoncore.kingdoms.ultimaaddon.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class DiscordRegulator {
    public static void giveRolesToMember(Player player, Role role){
        Member member = DiscordRegulator.getMember(player.getUniqueId());
        DiscordUtil.addRolesToMember(member, role);
    }

    public static void giveRolesToMembers(ArrayList<Member> members, ArrayList<Role> roles) {
        for (Member member : members) {
            for (Role role : roles) {
                DiscordUtil.addRoleToMember(member, role);
            }
        }
    }

    public static Member getMember(UUID PlayerId) {
        return DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(PlayerId));
    }

    public static void createRole(Player player, String name) {
        Member member = DiscordRegulator.getMember(player.getUniqueId());
        member.getGuild().createRole()
                .setName(name)
                .setColor(Color.getColor(Utils.hexGenerator()))
                .setMentionable(true).complete();
        String debugInfo = String.format("Роль %s основанная на игроке %s была создана, discordId: %s", name, player, member);
        Bukkit.getLogger().info(debugInfo);
    }

    public static void removeRolesFromMember(Player player) {

    }

    public static void removeRoleFromAllMembers(Role role, UUID... members) {
        for (UUID member : members) {
            DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(member), role);
            return;
        }
    }
    // Тщетная попытка реализовать переименовывание
//    public static void renameRole(Role oldRoleName, Role newRoleName) {
//        oldRoleName.delete().queue();
//        createRole(event)
//    }
}
