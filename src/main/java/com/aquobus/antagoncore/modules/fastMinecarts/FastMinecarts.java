package com.aquobus.antagoncore.modules.fastMinecarts;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastMinecarts implements Listener {
    private static final double VANILLA_MAX_SPEED = 0.4;
    private final Map<Material, Double> blockMaxSpeeds = new HashMap<>();
    private final List<Material> railTypes = List.of(
            Material.RAIL, Material.POWERED_RAIL,
            Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL
    );

    private final AntagonCore plugin;

    public FastMinecarts(AntagonCore plugin) {
        this.plugin = plugin;
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void loadConfig() {
        ConfigurationSection minecartSpeed = plugin.getConfig().getConfigurationSection("minecartSpeed");
        if (minecartSpeed == null) return;

        blockMaxSpeeds.clear();
        for (String key : minecartSpeed.getKeys(false)) {
            Material material = Material.getMaterial(key);
            if (material != null) {
                blockMaxSpeeds.put(material, minecartSpeed.getDouble(key));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart)) return;

        Minecart minecart = (Minecart) event.getVehicle();
        if (minecart.isEmpty() || !(minecart.getPassengers().get(0) instanceof Player)) return;

        Block railBlock = event.getVehicle().getLocation().getBlock();
        if (!railTypes.contains(railBlock.getType())) return;

        Block blockBelow = railBlock.getRelative(0, -1, 0);
        double blockMultiplier = blockMaxSpeeds.getOrDefault(blockBelow.getType(), VANILLA_MAX_SPEED);
        minecart.setMaxSpeed(blockMultiplier);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getVehicle() instanceof Minecart)) return;
        if (!(event.getExited() instanceof Player)) return;

        Minecart minecart = (Minecart) event.getVehicle();
        if (minecart.getMaxSpeed() > VANILLA_MAX_SPEED) {
            minecart.setMaxSpeed(VANILLA_MAX_SPEED);
        }
    }
}
