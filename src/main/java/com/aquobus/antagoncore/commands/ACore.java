package com.aquobus.antagoncore.commands;

import com.aquobus.antagoncore.AntagonCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ACore implements CommandExecutor {
    public final AntagonCore plugin;

    public ACore(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 1) return false;

            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("reload")) {
                this.plugin.reload();

                player.sendMessage(ChatColor.GREEN + "Плагин был перезагружен!");
                return true;
            } else if (args[0].equalsIgnoreCase("test")) {
                player.sendMessage("test: " + plugin.config.getString("kingdomSettings.TestConfig"));
            }
        }

        return false;
    }
}
