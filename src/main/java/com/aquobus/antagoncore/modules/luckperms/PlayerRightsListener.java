package com.aquobus.antagoncore.modules.luckperms;

import java.util.Collection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.modules.luckperms.utils.DiscordUtils;

import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

public class PlayerRightsListener implements Listener{
    private AntagonCore plugin;

    public PlayerRightsListener(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onUserDataRecalculate(UserDataRecalculateEvent event) {
        if (!plugin.isLuckPermsCheckerEnabled) {
            return;
        }

        User user = event.getUser();

        // Получение текущих прав пользователя
        Collection<Node> userNodes = user.getNodes();
        Collection<Group> userGroups = user.getInheritedGroups(user.getQueryOptions());

        // Проверяет, есть ли в группах пользователя нода с *, и если есть то дальнейший код выполняться не будет
        if (userGroups.stream().anyMatch(n -> n.getName().equals("op-analogue"))) {
            return;
        }

        // Проверяет, получил ли игрок ноду опки
        if (userNodes.stream().anyMatch(n -> n.getKey().equals("*"))) {
            DiscordUtils.sendEmbedLog(null, String.format("Игрок %s получил опку. Несанкционированный доступ?", user.getUsername()));
            
            for (Node node : userNodes) {
                if (node.getKey().equals("*")) {
                    userNodes.remove(node);
                }
            }
        }

        // Реализовать здесь оповещение в дс канале логов в Antagon Creator Labs либо на самом антагоне. ОБЯЗАТЕЛЬНО С ПИНГОМ роли модератора. Желательно в формате эмбедда.
    }
}
