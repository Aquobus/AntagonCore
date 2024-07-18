/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aquobus.antagoncore.discord_bot;

import com.aquobus.antagoncore.AntagonCore;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonInteraction;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class DiscordCommands implements Listener, SlashCommandProvider {

    public static final String ECHO_LABEL = "echo";
    public static final String ECHO_EXTEND_LABEL = "echoextend";

    public static final Set<String> DISCORD_COMMANDS;

    static {
        Set<String> discordCommands = new HashSet<>();
        for (Field field : DiscordCommands.class.getFields()) {
            if (field.getType().equals(String.class) && field.getName().endsWith("_LABEL")) {
                try {
                    discordCommands.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        DISCORD_COMMANDS = Collections.unmodifiableSet(discordCommands);
    }

//    private final DiscordSRV discordsrv;
//
//    public DiscordCommands(DiscordSRV discordsrv) {
//        this.discordsrv = discordsrv;
//    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        Guild guild = DiscordSRV.getPlugin().getMainGuild();
        List<CommandData> commandDataList = new ArrayList<>();

        commandDataList.add(new CommandData(ECHO_LABEL, "Повторяет введенный текст")
                .addOption(OptionType.STRING,"text","Text to repeat"));
        commandDataList.add(new CommandData(ECHO_EXTEND_LABEL, "Повторяет введенный текст + кнопки")
                .addOption(OptionType.STRING,"text","Text to repeat"))
        ;

        return commandDataList.stream().map(each -> new PluginSlashCommand(AntagonCore.plugin, each, guild.getId())).collect(Collectors.toSet());
    }

    public void reload() {
        DiscordSRV.api.updateSlashCommands();
    }

    @SlashCommand(path = "echo", deferReply = true)
    public void echoCommand(SlashCommandEvent event) {
        String replyText = event.getOption("text").getAsString().replaceAll("\\n","\n");
        event.getHook().sendMessage(replyText).queue();
    }

    @SlashCommand(path = "echoextend", deferReply = true)
    public void echoextendCommand(SlashCommandEvent event) {
        //Emoji tiktok = Emoji.fromEmote(DiscordSRV.getPlugin().getMainGuild().getEmoteById("<:tiktok:946499645020979210>"));
        //Emoji youtube = Emoji.fromEmote(DiscordSRV.getPlugin().getMainGuild().getEmoteById("<:youtube:946499645020979210>"));
        String replyText = event.getOption("text").getAsString().replaceAll("\\n","\n");

        event.getHook().sendMessage(replyText)
                .addActionRow(
                        Button.primary("invite","📩Discord"),
                        //Button.link("https://www.youtube.com/@AntagonCreators","Youtube").withEmoji(youtube),
                        Button.link("https://www.youtube.com/@AntagonCreators","Youtube"),
                        Button.link("https://www.youtube.com/@AntagonCreators","Tiktok")
                ).queue();
    }
//    @Subscribe(priority = ListenerPriority.HIGHEST)
//    public void onButtonInteraction(ButtonInteraction event) {
//        if (event.getComponentId().equals("invite")) {
//            event.deferReply().queue();
//            event.getHook()
//                    .setEphemeral(true)
//                    .sendMessage("[Ссылка на наш дискорд: https://discord.com/invite/ShkXXvSH9K](https://discord.com/invite/ShkXXvSH9K)")
//                    .queue();
//            return;
//        }
//    }
    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("invite")) {
            event.deferReply().queue();
            event.getHook()
                    .setEphemeral(true)
                    .sendMessage("[Ссылка на наш дискорд: https://discord.com/invite/ShkXXvSH9K](https://discord.com/invite/ShkXXvSH9K)")
                    .queue();
            return;
        }
    }
}
