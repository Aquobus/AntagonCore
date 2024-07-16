package com.aquobus.antagoncore.discord_bot;

import com.aquobus.antagoncore.AntagonCore;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonInteraction;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class slash_commands extends ListenerAdapter implements Listener, SlashCommandProvider {
    private final AntagonCore plugin;
    public slash_commands(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return new HashSet<>(Arrays.asList(
//                // bests
//                new PluginSlashCommand(plugin, new CommandData("best", "Best _____")
//                        .addSubcommands(new SubcommandData("friend", "Best friend"))
//                        .addSubcommands(new SubcommandData("plugin", "Best plugin"))
//                ),
//                // linked account
//                new PluginSlashCommand(plugin, new CommandData("linked", "Check the Minecraft account you have linked with your Discord")),
                //echo
                new PluginSlashCommand(plugin, new CommandData("echo", "Дублирует ваш текст от имени бота")
                        .addOption(OptionType.STRING,"Текст","То что нужно продублировать")
                ),
                new PluginSlashCommand(plugin, new CommandData("echoExtend", "Дублирует текст и добавляет кнопки")
                        .addOption(OptionType.STRING,"Текст","То что нужно продублировать")
                )
        ));
    }

//    @SlashCommand(path = "best/plugin")
//    public void bestPlugin(SlashCommandEvent event) {
//        event.reply("DiscordSRV!").queue();
//    }
//    @SlashCommand(path = "best/friend")
//    public void bestFriend(SlashCommandEvent event) {
//        event.reply("Dogs!").queue();
//    }
//
//    @SlashCommand(path = "linked", deferReply = true, deferEphemeral = true)
//    public void linkedCommand(SlashCommandEvent event) {
//        UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getUser().getId());
//        if (uuid != null) {
//            event.getHook().sendMessage("✅ Your account is linked to " + Bukkit.getOfflinePlayer(uuid).getName() + ".").queue();
//        } else {
//            event.getHook().sendMessage("❌ Your account is not linked.").queue();
//        }
//    }

    //@Override
    @Subscribe
    public void onButtonInteraction(ButtonInteraction event) {
        if (event.getComponentId().equals("invite")) {
            event.reply("Наш дискорд: [https://discord.com/invite/ShkXXvSH9K](https://discord.com/invite/ShkXXvSH9K)").setEphemeral(true).queue();
        }
    }

    @SlashCommand(path = "echo", deferReply = true)
    public void echoCommand(SlashCommandEvent event) {
        String replyText = Objects.requireNonNull(event.getOption("Текст")).getAsString();
        event.reply(replyText).complete();
    }

    @SlashCommand(path = "echoExtend", deferReply = true)
    public void echoExtendCommand(SlashCommandEvent event) {
        Button invite = Button.primary("invite","📩Discord");
        Button youtube = Button.link("https://www.youtube.com/@AntagonCreators","📩Youtube");
        String replyText = Objects.requireNonNull(event.getOption("Текст")).getAsString();
        event.reply(replyText).addActionRow(invite,youtube).queue();
    }
}
