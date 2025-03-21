package com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.utils;

import java.util.UUID;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kingdoms.config.KingdomsConfig;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.constants.metadata.KingdomMetadata;
import org.kingdoms.constants.metadata.KingdomsObject;
import org.kingdoms.constants.metadata.StandardKingdomMetadata;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.events.lands.UnclaimLandEvent;

import com.aquobus.antagoncore.AntagonCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.kingdoms.utils.time.TimeFormatter;

import java.util.Random;

import static com.aquobus.antagoncore.AntagonCore.plugin;

public class Utils {
    public static String convertAmps(String s) {
        return s.replaceAll("&", "§");
    }

    public static long getWarTime() {
        return KingdomsConfig.Invasions.CHALLENGES_DURATION.getManager().getTimeMillis();
    }

    public static long getNewbieTime() {
        return KingdomsConfig.CREATION_KINGDOMS_NEWBIE_PROTECTION.getManager().getTimeMillis();
    }

    public static String getLastChallenge(Kingdom kingdom) {
        StandardKingdomMetadata skm = (StandardKingdomMetadata) kingdom.getMetadata().get("last_challenge");
        return skm == null ? null : skm.getString();
    }

    public static Component toComponent(String line) {
        return LegacyComponentSerializer.legacySection().deserialize(convertAmps(line)).decoration(TextDecoration.ITALIC, false);
    }

    public static void msg(Player player, String s) {
        player.sendMessage(toComponent(s));
    }

    public static void schedule(int ticks, Runnable r) {
        Bukkit.getScheduler().runTaskLater(AntagonCore.getPlugin(), r, ticks);
    }

    public static void scheduleAsync(int ticks, Runnable r) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(AntagonCore.getPlugin(), r, ticks);
    }
    public static boolean hasChallenged(Kingdom kingdom) {
        long wartime = Utils.getWarTime();
        long ctime = System.currentTimeMillis();
        String lastChallenge = Utils.getLastChallenge(kingdom);
        if (lastChallenge != null) {
            String[] lcs = lastChallenge.split("@");
            if (Kingdom.getKingdom(UUID.fromString(lcs[0])) != null 
                    && ctime < Long.parseLong(lcs[1]) + wartime) {
                return true;
            }
        }
        
        for (Entry<UUID, Long> e : kingdom.getChallenges().entrySet()) {
            if (Kingdom.getKingdom(e.getKey()) != null && 
                    ctime < e.getValue() + wartime) {
                return true;
            }
        }
        
        return false;
    }

    public static <T> int unclaimOutpost(KingdomPlayer kp, Kingdom k, KingdomsObject<T> l) {
        KingdomMetadata outpostdata = l.getMetadata().get("outpost_id");
        if (outpostdata == null) {
            return 0;
        }

        long outpostid = ((StandardKingdomMetadata) outpostdata).getLong();
        Set<SimpleChunkLocation> toUnclaim = new HashSet<>();
        k.getLands().forEach(kl -> {
            KingdomMetadata kld = kl.getMetadata().get("outpost_id");
            if (kld == null) {
                return;
            }

            if (((StandardKingdomMetadata) kld).getLong() != outpostid) {
                return;
            }

            toUnclaim.add(kl.getLocation());
        });

        if (toUnclaim.isEmpty()) {
            return 0;
        }

        Bukkit.getScheduler().runTask(AntagonCore.getPlugin(), () -> k.unclaim(new HashSet<>(toUnclaim), kp, UnclaimLandEvent.Reason.ADMIN, kp != null));

        return toUnclaim.size();
    }

    public static String hexGenerator() {
        return "#" + String.format("%06x", new Random().nextInt(0xffffff + 1));
    }

    public static String formatDate(long i) {
        return TimeFormatter.ofRaw(i);
    }

    public static String toPlain(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static String getConfigString(String path) {
        return plugin.getConfig().getString(path);
    }

    public static void saveEntryToStorage(String keyString, Long role) {
        plugin.reloadConfig();

        plugin.getConfig().set(keyString, role);
        plugin.saveConfig();
    }

    public static void removeEntryFromStorage(String keyString) {
        plugin.reloadConfig();

        plugin.getConfig().set(keyString, null);
        plugin.saveConfig();
    }
}
