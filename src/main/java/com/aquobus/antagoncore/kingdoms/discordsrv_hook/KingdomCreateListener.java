package com.aquobus.antagoncore.kingdoms.discordsrv_hook;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.*;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.DiscordRegulator;

import org.kingdoms.events.members.KingdomJoinEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;
public class KingdomCreateListener implements Listener {
    private final Role roleOnCreation;
    private final HashMap<Kingdom, Role> kingdomRoles = new HashMap<>();
    private final HashMap<Kingdom, KingdomPlayer> actualKings = new HashMap<>();

    public KingdomCreateListener(AntagonCore plugin) {
        roleOnCreation = DiscordUtil.getRole(plugin.config.getString("kingdomSettings.giveDiscordRoleOnKingdomCreation"));
    }

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = kingdom.getKing().getPlayer();
        assert player != null;
        Member member = DiscordRegulator.getMember(player.getUniqueId());

        Role role = DiscordRegulator.createRole(member, kingdom.getName());
        DiscordUtil.addRolesToMember(member, roleOnCreation);

        kingdomRoles.put(kingdom, role);
        actualKings.put(kingdom, kingdom.getKing());

        // assert player != null;
        // Выдать главе клана роль
        // UUID kingId = player.getUniqueId();
        // String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(kingId);
        // Member discordMember = DiscordUtil.getMemberById(discordId);
        
        // Создать роль "Название клана"
        // TODO: ВЫВЕСТИ ПЕРЕМЕННУЮ В КОНФИГ
        // messages.roleCreationOnKingdomCreate = "Роль {created_role} была успешно создана"
        // config.get("messages.roleCreationOnKingdomCreate").toString().replace("{created_role}", kingdom.getName());
        // discordMember.getGuild().createRole()
        //         .setName(kingdom.getName())
        //         .setColor(Color.getColor(Utils.hexGenerator()))
        //         .setMentionable(true)
        //         .queue(role -> {
        //             Bukkit.getLogger().info("Роль для клана была создана");
        //             kingdomRoles.put(kingdom, role); // Store the role in the HashMap
        //         });
    }

    @EventHandler
    public void onKingdomDisband(KingdomDisbandEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        Player player = kingdom.getKing().getPlayer();
        assert player != null;
        Member member = DiscordRegulator.getMember(player.getUniqueId());
        DiscordUtil.removeRolesFromMember(member, roleOnCreation);

        // Удаление роли "Название Клана" у всех участников
        Role memberRole = kingdomRoles.get(kingdom);
        UUID members[] = kingdom.getMembers().toArray(new UUID[0]);
        // for (UUID eachMember : members) {
        //     Member eachDMember = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(eachMember));
        //     DiscordUtil.removeRolesFromMember(eachDMember, memberRole);
        // }
        DiscordRegulator.removeRoleFromAllMembers(memberRole, members);
    }

    @EventHandler
    public void onKingdomKingChange(KingdomKingChangeEvent event) {
        // UUID oldKing = Objects.requireNonNull(event.getPlayer()).getId();
        // UUID newKing = Objects.requireNonNull(event.getNewKing().getId());
        // Member discordOldKing = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(oldKing));
        // Member discordNewKing = DiscordUtil.getMemberById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(newKing));
        // DiscordUtil.removeRolesFromMember(discordOldKing, roleOnCreation);
        // DiscordUtil.addRoleToMember(discordNewKing, roleOnCreation);

        Kingdom kingdom = event.getKingdom();
        KingdomPlayer oldKing = actualKings.get(kingdom);
        KingdomPlayer newKing = event.getNewKing();

        DiscordUtil.removeRolesFromMember(DiscordRegulator.getMember(oldKing.getId()), roleOnCreation);
        DiscordUtil.addRoleToMember(DiscordRegulator.getMember(newKing.getId()), roleOnCreation);

        actualKings.replace(kingdom, newKing);
    }

    @EventHandler
    public void onGroupRenameEvent(GroupRenameEvent event) {
        // String oldName = event.getOldName();
        // String newName = event.getNewName();
        // Написать замену роли участников на новое название
    }

    @EventHandler
    public void onKingdomJoin(KingdomJoinEvent event) {
        event.getPlayer();
        // Написать добавление роли при входе в клан
    }

    @EventHandler
    public void onKingdomLeave(KingdomLeaveEvent event) {
        // Написать удаление роли у участника
    }
}