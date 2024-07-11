package com.aquobus.antagoncore.kingdoms.discordsrv_hook;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.events.general.*;

import com.aquobus.antagoncore.AntagonCore;
import org.kingdoms.events.members.KingdomJoinEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class KingdomCreateListener implements Listener {
    private final Role roleOnCreation;

    public KingdomCreateListener(AntagonCore plugin) {
        roleOnCreation = DiscordUtil.getRole(plugin.config.getString("kingdomSettings.giveDiscordRoleOnKingdomCreation"));
    }

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = kingdom.getKing().getPlayer();
        assert player != null;
        // Выдать главе клана роль
        UUID kingId = player.getUniqueId();
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(kingId);
        Member discordMember = DiscordUtil.getMemberById(discordId);
        DiscordUtil.addRoleToMember(discordMember, roleOnCreation);
        // Создать роль "Название клана"
        // FIXME: ВЫВЕСТИ ПЕРЕМЕННУЮ В КОНФИГ
        // messages.roleCreationOnKingdomCreate = "Роль {created_role} была успешно создана"
        // config.get("messages.roleCreationOnKingdomCreate").toString().replace("{created_role}", kingdom.getName());
        discordMember.getGuild().createRole()
                .setName(kingdom.getName())
                .setColor(Color.getColor("#445166"))
                .setMentionable(true)
                .queue(role -> Bukkit.getLogger().info("Роль для клана была создана"));
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

        // Удаление роли "Название Клана" у всех участников
        Set<UUID> members = kingdom.getMembers();
        for (UUID member : members) {
            Member eachDMember = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(member));
            DiscordUtil.removeRolesFromMember(eachDMember, roleOnCreation);
        }
    }

    @EventHandler
    public void onKingdomKingChange(KingdomKingChangeEvent event) {
        UUID oldKing = Objects.requireNonNull(event.getPlayer()).getId();
        UUID newKing = Objects.requireNonNull(event.getNewKing().getId());
        Member discordOldKing = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(oldKing));
        Member discordNewKing = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(newKing));
        DiscordUtil.removeRolesFromMember(discordOldKing, roleOnCreation);
        DiscordUtil.addRoleToMember(discordNewKing, roleOnCreation);
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
        // Написать удаление роли у участника
    }
}