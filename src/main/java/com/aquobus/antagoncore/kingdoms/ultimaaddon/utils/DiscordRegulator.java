package com.aquobus.antagoncore.kingdoms.ultimaaddon.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.kingdoms.constants.group.Kingdom;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

import static com.aquobus.antagoncore.AntagonCore.plugin;
public class DiscordRegulator {
//    /**
//     Добавляет роли каждому участнику списка из списка ролей
//     @param members ArrayList из мембером класса Member
//     @param roles ArrayList из ролей класса Role
//     */
//    public static void addRolesToMembers(ArrayList<Member> members, ArrayList<Role> roles) {
//        for (Member member : members) {
//            for (Role role : roles) {
//                DiscordUtil.addRoleToMember(member, role);
//            }
//        }
//    }
    /**
     @param PlayerUUID UUID игрока класса Player
     @return Класс Member
     */
    public static Member getMember(UUID PlayerUUID) {
        return DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(PlayerUUID));
    }
    /**
     Создаёт роль с названием клана, выставляет ей случайный цвет, сохраняет ID роли в конфиге плагина
     @param kingdom Kingdom королевство для создания роли клана
     */
    public static void createRole(Kingdom kingdom, String reason) {
        Member member = DiscordRegulator.getMember(kingdom.getKingId());
        Role role = member.getGuild().createRole()
                .setName(kingdom.getName())
                .setColor(Color.getColor(Utils.hexGenerator()))
                .setMentionable(true)
                .reason(reason)
                // Если не сработает создание заменить это полотно на complete() и вывести логи ниже
                .complete();
        
        String storage = String.format("storage.%s.roleID: %s",kingdom.getId(), role);
        saveToStorage(storage);
        
        Bukkit.getLogger().info(String.format("AntagonCORE: Запись в конфиг | %s", storage));
        Bukkit.getLogger().info(String.format("AntagonCORE: Создана роль | Role: %s Name: %s", role, role.getName()));
    }

    /**
     Создаёт роль с названием клана, выставляет ей случайный цвет, сохраняет ID роли в конфиге плагина
     @param kingdom Kingdom королевство для создания роли клана
     */
    public static void renameRole(Kingdom kingdom) {
        Role role = DiscordUtil.getRole(plugin.getConfig().getString(String.format("storage.%s.roleID",kingdom.getId())));
        role.delete().reason("Переименовывание клана");

        String storage = String.format("storage.%s.roleID: %s", kingdom.getId(), role);
        saveToStorage(storage);

        Utils.scheduleAsync(100, () -> createRole(kingdom,"Переименовывание клана"));
    }

    public static void saveToStorage(String storage) {
        try {
            plugin.getConfig().save(storage);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
