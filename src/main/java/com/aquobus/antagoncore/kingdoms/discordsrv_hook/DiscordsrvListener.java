// Весь листенер отвечает за то чтобы добавить дополнительное взаимодействие с дискордом для кингдомсов

package com.aquobus.antagoncore.kingdoms.discordsrv_hook;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.DiscordRegulator;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.events.general.GroupRenameEvent;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.general.KingdomDisbandEvent;
import org.kingdoms.events.general.KingdomKingChangeEvent;
import org.kingdoms.events.members.KingdomJoinEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.Objects;
import java.util.UUID;

import static com.aquobus.antagoncore.AntagonCore.plugin;

public class DiscordsrvListener implements Listener {
    private final Role roleOnCreation;

    public DiscordsrvListener(AntagonCore plugin) {
        roleOnCreation = DiscordUtil.getRole(plugin.config.getString("kingdomSettings.giveDiscordRoleOnKingdomCreation"));
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        UUID playerUUID = Objects.requireNonNull(kingdom.getKing().getPlayer()).getUniqueId();
        // .createRole() создаёт роль и записывает в хранилище
        DiscordRegulator.createRole(kingdom, "Создание клана");
        DiscordUtil.addRoleToMember(DiscordRegulator.getMember(playerUUID), roleOnCreation);
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomDisband(KingdomDisbandEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        // Удаляем роль у Лидера клана
        Player king = Objects.requireNonNull(kingdom.getKing().getPlayer());
        DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(king.getUniqueId()), roleOnCreation);
        // Удаляем роль "Название клана" у каждого участника
        Role role = DiscordUtil.getRole(plugin.getConfig().getString(String.format("storage.%s.roleID",kingdom.getId())));
        role.delete().reason("Удаление клана"); //.complete() ??
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomKingChange(KingdomKingChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        UUID oldKingUUID = Objects.requireNonNull(event.getKingdom()).getKing().getId();
        UUID newKingUUID = event.getNewKing().getId();
        
        DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(oldKingUUID), roleOnCreation);
        DiscordUtil.addRoleToMember(DiscordRegulator.getMember(newKingUUID), roleOnCreation);
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onGroupRenameEvent(GroupRenameEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String newName = Objects.requireNonNull(event.getNewName());
        Kingdom newKingdom = Objects.requireNonNull(Kingdom.getKingdom(newName));

        DiscordRegulator.renameRole(newKingdom);
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomJoin(KingdomJoinEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Выдать роль "Название клана" зашедшему игроку
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = Objects.requireNonNull(event.getPlayer().getPlayer());
        Role role = DiscordUtil.getRole(plugin.getConfig().getString(String.format("storage.%s.roleID",kingdom.getId())));

        DiscordUtil.addRoleToMember(DiscordRegulator.getMember(player.getUniqueId()), role);
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Забрать роль "Название клана" у ливнувшего игрока
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = Objects.requireNonNull(event.getPlayer().getPlayer());
        Role role = DiscordUtil.getRole(plugin.getConfig().getString(String.format("storage.%s.roleID",kingdom.getId())));

        DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(player.getUniqueId()), role);
    }
}