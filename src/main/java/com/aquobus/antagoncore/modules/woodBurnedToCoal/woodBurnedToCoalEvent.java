package com.aquobus.antagoncore.modules.woodBurnedToCoal;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.inventory.ItemStack;

import com.aquobus.antagoncore.AntagonCore;

public class woodBurnedToCoalEvent implements Listener {
    private final AntagonCore plugin;
    private final List<Material> BURNABLE_BLOCKS = Arrays.asList(
        Material.OAK_WOOD,
        Material.SPRUCE_WOOD,
        Material.BIRCH_WOOD,
        Material.ACACIA_WOOD,
        Material.JUNGLE_WOOD,
        Material.DARK_OAK_WOOD
    );

    public woodBurnedToCoalEvent(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void woodBurnListener(BlockBurnEvent event) {
        if (!(plugin.isWoodBurnedToCoalEnabled)) {
            return;
        }

        Block block = event.getBlock();

        if (BURNABLE_BLOCKS.contains(block.getType())) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.CHARCOAL));
        }
    }
}
