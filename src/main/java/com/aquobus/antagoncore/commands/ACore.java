package com.aquobus.antagoncore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aquobus.antagoncore.AntagonCore;

import net.md_5.bungee.api.ChatColor;

public class ACore implements CommandExecutor {
    public final AntagonCore plugin;

    public ACore(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 2) return false;

            String argument = args[0];

            Player player = (Player) sender;
            if (argument.toLowerCase() == "reload") {
                this.plugin.reload();

                player.sendMessage(ChatColor.GREEN + "The plugin and it's config were reloaded!");
                return true;
            }
        }

        return false;
    }
}
