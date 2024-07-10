package com.aquobus.antagoncore.kingdoms.discordsrv_hook;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.general.KingdomCreateEvent;

import com.aquobus.antagoncore.AntagonCore;

public class KingdomCreateListener implements Listener {
    private AntagonCore plugin;
    private Kingdom kingdom;
    private KingdomPlayer king;

    public KingdomCreateListener(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKingdomCreate(KingdomCreateEvent event) {
        kingdom = event.getKingdom();
        king = kingdom.getKing();
    }
}
