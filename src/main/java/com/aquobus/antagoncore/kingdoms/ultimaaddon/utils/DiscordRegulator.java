package com.aquobus.antagoncore.kingdoms.ultimaaddon.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.awt.*;
import java.util.UUID;

public class DiscordRegulator {
    // public void giveRoleToMember(Member member, Role role) {
    //     DiscordUtil.addRoleToMember(member, role);
    // }

        // public void giveRoles(Member member, Role... role) {
    //     for (Role roles : role) {
    //         DiscordUtil.addRoleToMember(member, roles);
    //     }
    // }

    public static Member getMember(UUID PlayerId) {
        Member discordMember = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(PlayerId));

        return discordMember;
    }

    public static Role createRole(Member member, String name) {
        Role role = member.getGuild().createRole()
            .setName(name)
            .setColor(Color.getColor(Utils.hexGenerator()))
            .setMentionable(true).complete();

        return role;
    }

    public static void removeRoleFromAllMembers(Role role, UUID... members) {
        for (UUID member : members) {
            DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(member), role);
        }
    }
}
