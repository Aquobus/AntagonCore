package com.aquobus.antagoncore.modules.lightningToGlass;

import com.aquobus.antagoncore.AntagonCore;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;

public class LightningToGlass implements Listener {
    private final AntagonCore plugin;

    public LightningToGlass(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        if (!plugin.isLightningToGlassEnabled) {
            return;
        }

        Location location = event.getLightning().getLocation();
        Block block = location.getBlock();

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block surroundingBlock = event.getLightning().getWorld().getBlockAt(x + dx, y-1, z + dz);
                if (surroundingBlock.getType() == Material.SAND || surroundingBlock.getType() == Material.SANDSTONE) {
                    surroundingBlock.setType(Material.GLASS);
                }
            }
        }
    }
}

