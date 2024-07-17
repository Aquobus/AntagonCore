// Весь листенер отвечает за то чтобы добавить дополнительное взаимодействие с дискордом для кингдомсов

package com.aquobus.antagoncore.kingdoms.discordsrv_hook;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.DiscordUtils;
import com.aquobus.antagoncore.kingdoms.ultimaaddon.utils.Utils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.metadata.StandardKingdomMetadata;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.GroupDisband;
import org.kingdoms.events.general.GroupRenameEvent;
import org.kingdoms.events.general.GroupShieldPurchaseEvent;
import org.kingdoms.events.general.KingdomCreateEvent;
import org.kingdoms.events.general.KingdomDisbandEvent;
import org.kingdoms.events.general.KingdomKingChangeEvent;
import org.kingdoms.events.general.KingdomPacifismStateChangeEvent;
import org.kingdoms.events.members.KingdomJoinEvent;
import org.kingdoms.events.members.KingdomLeaveEvent;
import org.kingdoms.gui.GUIAccessor;
import org.kingdoms.gui.InteractiveGUI;
import org.kingdoms.gui.KingdomsGUI;
import org.kingdoms.locale.KingdomsLang;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.aquobus.antagoncore.AntagonCore.plugin;

public class DiscordsrvListener implements Listener {
    private final Role roleOnCreation;
    private final TextChannel defaultKingdomsChannel = DiscordUtil.getTextChannelById(Utils.getConfigString("kingdomSettings.defaultKingdomsMessagesChannel"));
    private static Map<Player, Integer> cantClose = new HashMap<>();

    public DiscordsrvListener(AntagonCore plugin) {
        roleOnCreation = DiscordUtil.getRole(plugin.config.getString("kingdomSettings.giveDiscordRoleOnKingdomCreation"));
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        Kingdom kingdom = Objects.requireNonNull(event.getKingdom());
        UUID playerUUID = Objects.requireNonNull(kingdom.getKing().getPlayer()).getUniqueId();
        // .createRole() создаёт роль и записывает в хранилище
        DiscordUtils.createRole(kingdom, "Создание клана");
        DiscordUtil.addRoleToMember(DiscordUtils.getMember(playerUUID), roleOnCreation);
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
        DiscordUtil.removeRolesFromMember(DiscordUtils.getMember(king.getUniqueId()), roleOnCreation);
        // Удаляем роль "Название клана" у каждого участника
        DiscordUtils.removeRole(kingdom, "Удаление клана");
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onKingdomKingChange(KingdomKingChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        UUID oldKingUUID = Objects.requireNonNull(event.getKingdom()).getKing().getId();
        UUID newKingUUID = event.getNewKing().getId();
        
        DiscordUtil.removeRolesFromMember(DiscordUtils.getMember(oldKingUUID), roleOnCreation);
        DiscordUtil.addRoleToMember(DiscordUtils.getMember(newKingUUID), roleOnCreation);
    }
    // REVIEW: Проверить работоспособность
    @EventHandler
    public void onGroupRenameEvent(GroupRenameEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Kingdom kingdom = (Kingdom) Objects.requireNonNull(event.getGroup());
        String roleId = Utils.getConfigString(String.format("storage.%s.roleID", kingdom.getId()));
        DiscordUtil.getRole(roleId).getManager().setName(event.getNewName()).reason("Переименование клана").queue();
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

        // НЕ УДАЛЯТЬ, СДЕЛАНО ДЛЯ ТОГО ЧТОБЫ РОЛЬ УСПЕЛА СОЗДАТЬСЯ И ТОЛЬКО ПОТОМ ПРИСВОИЛАСЬ
        Utils.scheduleAsync(200, () -> {
            Role role = DiscordUtil.getRole(Utils.getConfigString(String.format("storage.%s.roleID", kingdom.getId() )));
            DiscordUtil.addRoleToMember(DiscordUtils.getMember(player.getUniqueId()), role);
        });
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
        Role role = DiscordUtil.getRole(Utils.getConfigString(String.format("storage.%s.roleID", kingdom.getId() )));

        DiscordUtil.removeRolesFromMember(DiscordUtils.getMember(player.getUniqueId()), role);
    }

    // -------------------------------------------------
    // ULTIMAADDONS FEATURES
    // -------------------------------------------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShield(GroupShieldPurchaseEvent event) {
        if (!(event.getGroup() instanceof Kingdom)) {
            return;
        }

        // Register next latest time to buy shield
        Kingdom k = (Kingdom) event.getGroup();
        long shieldtime = System.currentTimeMillis() + event.getShieldDuration() * 2;
        k.getMetadata().put(AntagonCore.shield_time, new StandardKingdomMetadata(shieldtime));
        String time = Utils.formatDate(event.getShieldDuration());
        DiscordUtils.sendMessage(defaultKingdomsChannel, ":shield: **" + k.getName() + "** активировал щит на время: " + time);
        Bukkit.getOnlinePlayers().forEach(p -> {
            Utils.msg(p, "&6" + k.getName() + " &2активировал щит на время: &6" + time);
        });

        // Close other shield buyers to stop abuse
        // k.getOnlineMembers().forEach(p -> Utils.closeInventory(p, "Shields", "Challenge"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisband(KingdomDisbandEvent event) {
        Kingdom k = event.getKingdom();
        // k.getOnlineMembers().forEach(p -> Utils.closeInventory(p, "Challenge"));
        String name = k.getName();
        if (event.getReason() == GroupDisband.Reason.INVASION) {
            DiscordUtils.sendMessage(defaultKingdomsChannel, ":dart: **" + name + "** было распущено так как их чанк с Нексусом был захвачен");
        } else if (event.getReason() == GroupDisband.Reason.INACTIVITY) {
            DiscordUtils.sendMessage(defaultKingdomsChannel, ":pencil: **" + name + "** было распущено из-за неактивности.");
        } else {
            DiscordUtils.sendMessage(defaultKingdomsChannel, ":pencil: **" + name + "** было распущено");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreate(KingdomCreateEvent event) {
        Kingdom k = event.getKingdom();
        DiscordUtils.sendMessage(defaultKingdomsChannel, ":fleur_de_lis: **" + k.getName() + "** было создано");
        openSelectionGUI(k);
    }

    // Open custom creation GUI that only closes once an option is selected
    private void openSelectionGUI(Kingdom k) {
        KingdomPlayer kp = k.getKing();
        Player player = kp.getPlayer();
        int att = cantClose.getOrDefault(player, 0) + 1;
        if (att > 10) {
            setAggressor(k, kp, player);
            Bukkit.getLogger().warning(player.getName() + " слишком много раз закрывал GUI и автоматически переключался на режим агрессора");
            return;
        }
        cantClose.put(player, att);
        
        InteractiveGUI gui = GUIAccessor.prepare(player, KingdomsGUI.KINGDOM$CREATE);
        gui.push("pacifist", () -> setPacifist(k, kp, player))
        .push("aggressor", () -> setAggressor(k, kp, player));
        gui.onClose(() -> Utils.schedule(1, () -> {
            // Should not happen
            if (!player.isOnline()) {
                setAggressor(k, kp, player);
                return;
            }
            
            if (cantClose.containsKey(player)) {
                openSelectionGUI(k);
            }
        }));
        gui.open();
    }
    
    private void setAggressor(Kingdom k, KingdomPlayer kp, Player player) {
        k.setPacifist(false);
        KingdomsLang.COMMAND_CREATE_AGGRESSOR.sendMessage(player);
        cantClose.remove(player);
        player.closeInventory();
        
        // Add shield if aggressor
        long shieldtime = k.getSince() + Utils.getNewbieTime();
        k.activateShield(shieldtime - System.currentTimeMillis());
        k.getMetadata().put(AntagonCore.shield_time, new StandardKingdomMetadata(shieldtime));
    }
    
    private void setPacifist(Kingdom k, KingdomPlayer kp, Player player) {
        k.setPacifist(true);
        KingdomsLang.COMMAND_CREATE_PACIFIST.sendMessage(player);
        cantClose.remove(player);
        player.closeInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPacifist(KingdomPacifismStateChangeEvent e) {
        String k = e.getKingdom().getName();
        String inGame;
        if (e.isPacifist()) {
            DiscordUtils.sendMessage(defaultKingdomsChannel, ":peace: **" + k + "** is now a pacifist Kingdom");
            inGame = "&6" + k + " &2is a pacifist Kingdom.";
        } else {
            DiscordUtils.sendMessage(defaultKingdomsChannel, ":fire: **" + k + "** is now an aggressor Kingdom");
            inGame = "&6" + k + " &2is now an aggressor Kingdom.";
        }
        
        Bukkit.getOnlinePlayers().forEach(p -> Utils.msg(p, inGame));
    }
}