package com.aquobus.antagoncore.commands;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ACore implements CommandExecutor {
    public final AntagonCore plugin;

    public ACore(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 1) return false;

            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("reload")) {
                this.plugin.reload();
                player.sendMessage(Color.GREEN + "Плагин был перезагружен!");
                return true;
            } else if (args[0].equalsIgnoreCase("test")) {
                player.sendMessage("test: " + plugin.config.getString("kingdomSettings.TestConfig"));
            }
        }

        return false;
    }
}
