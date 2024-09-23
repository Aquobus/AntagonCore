package com.aquobus.antagoncore.modules.multiUseBowl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.aquobus.antagoncore.AntagonCore;

public class MultiUseBowlListener implements Listener {
    private final AntagonCore plugin;

    public MultiUseBowlListener(AntagonCore plugin) {
        this.plugin = plugin;
    }

    public void UsingBowlEvent(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Material itemType = item.getType();

        if (!(plugin.isMultiBowlEnabled)) {
            return;
        }

        if (itemType == Material.MUSHROOM_STEW || itemType == Material.RABBIT_STEW) {
            ItemStack customBowl = new ItemStack(Material.WOODEN_HOE);
            ItemMeta meta = customBowl.getItemMeta();
            NamespacedKey durabilityKey = new NamespacedKey(plugin, "bowl_durability");

            PersistentDataContainer bowlData = meta.getPersistentDataContainer();
            int durability = bowlData.getOrDefault(durabilityKey, PersistentDataType.INTEGER, 0);
        }
    }
}
