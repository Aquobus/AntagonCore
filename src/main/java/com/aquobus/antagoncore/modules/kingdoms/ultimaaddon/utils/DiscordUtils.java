package com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.kingdoms.constants.group.Kingdom;

import java.awt.*;
import java.util.UUID;

import static com.aquobus.antagoncore.AntagonCore.plugin;
public class DiscordUtils {

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
        Member member = DiscordUtils.getMember(kingdom.getKingId());
        Role role = member.getGuild().createRole()
                .setName(kingdom.getName())
                .setColor(Color.getColor(Utils.hexGenerator()))
                .setMentionable(true)
                .reason(reason)
                // Если не сработает создание заменить это полотно на complete() и вывести логи ниже
                .complete();
        
        String storageKingdomEntry = String.format("storage.%s.roleID", kingdom.getId());
        Utils.saveEntryToStorage(storageKingdomEntry, role.getIdLong());
        
        Bukkit.getLogger().info(String.format("AntagonCORE: Запись в конфиг | %s", storageKingdomEntry));
        Bukkit.getLogger().info(String.format("AntagonCORE: Создана роль | Role: %s Name: %s", role, role.getName()));
    }

    public static void removeRole(Kingdom kingdom, String reason) {
        String storageKingdomEntry = String.format("storage.%s", kingdom.getId());
        Role role = DiscordUtil.getRole(plugin.getConfig().getString(String.format("storage.%s.roleID", kingdom.getId())));

        role.delete().reason(reason).complete();
        Utils.removeEntryFromStorage(storageKingdomEntry);

    }
}
