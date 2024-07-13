// Весь листенер отвечает за то чтобы добавить дополнительное взаимодействие с дискордом для кингдомсов

package com.aquobus.antagoncore.kingdoms.discordsrv_hook;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.DiscordRegulator;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Group;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupRenameEvent;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.general.KingdomDisbandEvent;
import org.kingdoms.events.general.KingdomKingChangeEvent;
import org.kingdoms.events.members.KingdomJoinEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
public class KingdomCreateListener implements Listener {
    private final Role roleOnCreation;
    private final HashMap<Group,Role> kingdomRoles = new HashMap<>();
    //private final HashMap<UUID,Role> roles = new HashMap<>();

    public KingdomCreateListener(AntagonCore plugin) {
        roleOnCreation = DiscordUtil.getRole(plugin.config.getString("kingdomSettings.giveDiscordRoleOnKingdomCreation"));
    }

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Group group = kingdom.getGroup();
        Player player = kingdom.getKing().getPlayer();
        assert player != null;

        DiscordRegulator.createRole(player, kingdom.getName());
        DiscordRegulator.giveRolesToMember(player, roleOnCreation);

        kingdomRoles.put(group, role);

        
        // Создать роль "Название клана"
        // TODO: ВЫВЕСТИ ПЕРЕМЕННУЮ В КОНФИГ
        // messages.roleCreationOnKingdomCreate = "Роль {created_role} была успешно создана"
        // config.get("messages.roleCreationOnKingdomCreate").toString().replace("{created_role}", kingdom.getName());
        //         .queue(role -> {
        //             Bukkit.getLogger().info("Роль для клана была создана");
        //             kingdomRoles.put(kingdom, role); // Store the role in the HashMap
        //         });
    }

    @EventHandler
    public void onKingdomDisband(KingdomDisbandEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Group group = kingdom.getGroup();
        Player player = kingdom.getKing().getPlayer();
        assert player != null;
        Member member = DiscordRegulator.getMember(player.getUniqueId());

        // Удаление роли "Название Клана" у всех участников
        Role memberRole = kingdomRoles.get(kingdom);
        UUID[] members = kingdom.getMembers().toArray(new UUID[0]);
        // Удаляем роль у Лидера клана
        DiscordUtil.removeRolesFromMember(member, roleOnCreation);
        // Удаляем роль "Название клана" у каждого участника
        DiscordRegulator.removeRoleFromAllMembers(memberRole, members);

        kingdomRoles.remove(group);
    }

    @EventHandler
    public void onKingdomKingChange(KingdomKingChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        KingdomPlayer oldKing = Objects.requireNonNull(event.getKingdom()).getKing();
        KingdomPlayer newKing = event.getNewKing();

        DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(oldKing.getId()), roleOnCreation);
        DiscordUtil.addRoleToMember(DiscordRegulator.getMember(newKing.getId()), roleOnCreation);
    }

    @EventHandler
    public void onGroupRenameEvent(GroupRenameEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // TODO: Написать замену роли участников на новое название
        String oldName = event.getOldName();
        String newName = event.getNewName();
        // Переименовать саму роль
        // Переделать в базе
    }

    @EventHandler
    public void onKingdomJoin(KingdomJoinEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Выдать роль "Название клана" зашедшему игроку
        Player player = event.getPlayer().getPlayer();
        assert player != null;
        DiscordRegulator.giveRolesToMember(player, roleOnCreation);
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Выдать роль "Название клана" зашедшему игроку
        Player player = event.getPlayer().getPlayer();
        assert player != null;
        DiscordRegulator.removeRolesFromMember(player, roleOnCreation);
    }
}