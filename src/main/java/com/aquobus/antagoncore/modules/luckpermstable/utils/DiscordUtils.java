package com.aquobus.antagoncore.modules.luckpermstable.utils;

import javax.annotation.Nullable;
import java.awt.Color;
import java.time.Instant;

import com.aquobus.antagoncore.AntagonCore;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

public class DiscordUtils {
    private static AntagonCore plugin = AntagonCore.getPlugin();
    private static long logChannelId = plugin.getConfig().getLong("");
    private static long adminRoleId = plugin.getConfig().getLong("");

    @SuppressWarnings("null")
    public static void sendEmbedLog(@Nullable String title, String description) {
        TextChannel logChannel = DiscordSRV.getPlugin().getMainGuild().getTextChannelById(logChannelId);
        Role adminRole = DiscordSRV.getPlugin().getMainGuild().getRoleById(adminRoleId);

        if (title.equalsIgnoreCase("") || title.equalsIgnoreCase(null)) {
            title = "AntagonCore логи";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(title)
            .setDescription(description)
            .setColor(Color.RED)
            .setTimestamp(Instant.now());

        logChannel.sendMessage(adminRole.getAsMention()).setEmbeds(embedBuilder.build());
    }
}
