package com.aquobus.antagoncore.discord_bot;

import com.aquobus.antagoncore.AntagonCore;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonInteraction;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public class slash_commands extends ListenerAdapter implements Listener, SlashCommandProvider {
    private final AntagonCore plugin;
    public slash_commands(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        DiscordSRV.getPlugin().getJda().updateCommands().addCommands(
                new CommandData("echo", "Дублирует ваш текст от имени бота")
                        .addOption(OptionType.STRING,"Текст","То что нужно продублировать"),
                new CommandData("echoExtend", "Дублирует текст и добавляет кнопки")
                        .addOption(OptionType.STRING,"Текст","То что нужно продублировать")
        ).queue();
        return null;
    }

    //@Override
    @Subscribe
    public void onButtonInteraction(ButtonInteraction event) {
        if (event.getComponentId().equals("invite")) {
            event.reply("Наш дискорд: [https://discord.com/invite/ShkXXvSH9K](https://discord.com/invite/ShkXXvSH9K)").setEphemeral(true).queue();
        }
    }

//    @SlashCommand(path = "echo", deferReply = true)
//    public void echoCommand(SlashCommandEvent event) {
//        String replyText = Objects.requireNonNull(event.getOption("Текст")).getAsString();
//        event.reply(replyText).complete();
//    }
//
//    @SlashCommand(path = "echoExtend", deferReply = true)
//    public void echoExtendCommand(SlashCommandEvent event) {
//        Button invite = Button.primary("invite","📩Discord");
//        Button youtube = Button.link("https://www.youtube.com/@AntagonCreators","📩Youtube");
//        String replyText = Objects.requireNonNull(event.getOption("Текст")).getAsString();
//        event.reply(replyText).addActionRow(invite,youtube).queue();
//    }

    public void onSlashCommandInteraction(SlashCommandEvent event) {
        if (event.getName().equals("echo")) {
            String replyText = Objects.requireNonNull(event.getOption("Текст")).getAsString();
            event.reply(replyText).queue(); // reply immediately
        }
        if (event.getName().equals("echoExtend")) {
            Button invite = Button.primary("invite","📩Discord");
            Button youtube = Button.link("https://www.youtube.com/@AntagonCreators","📩Youtube");
            String replyText = Objects.requireNonNull(event.getOption("Текст")).getAsString();
            event.reply(replyText).addActionRow(invite,youtube).queue();
        }
    }
}
