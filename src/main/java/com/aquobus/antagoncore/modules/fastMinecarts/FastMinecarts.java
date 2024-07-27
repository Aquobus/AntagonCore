package com.aquobus.antagoncore.modules.fastMinecarts;

import com.aquobus.antagoncore.AntagonCore;
import org.bukkit.Material;
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
    private final ConfigurationSection minecartSpeed;

    public FastMinecarts(AntagonCore plugin) {
        minecartSpeed = plugin.config.getConfigurationSection("minecartSpeed");
    }

    private final double VANILLA_MAX_SPEED = 0.4;
    private final Map<Material, Double> _blockMaxSpeeds = new HashMap<>();
    private final List<Material> railTypes = List.of(
            Material.RAIL, Material.POWERED_RAIL,
            Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL
    );

    public void loadFastMinecartsConfig() {
        if (minecartSpeed == null) return;
        _blockMaxSpeeds.clear();
        for (String key : minecartSpeed.getKeys(false)) {
            Material material = Material.getMaterial(key);
            if (material != null) {
                _blockMaxSpeeds.put(material, minecartSpeed.getDouble(key));
            }
        }
        for (String key : minecartSpeed.getKeys(false)) {
            Material material = Material.getMaterial(key);
            if (material != null) {
                _blockMaxSpeeds.put(material, minecartSpeed.getDouble(key));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart)) return;

        Minecart minecart = (Minecart) event.getVehicle();
        if (minecart.isEmpty()) return;
        if (!(minecart.getPassengers().get(0) instanceof Player)) return;

        Material railBlockType = event.getVehicle().getLocation().getBlock().getType();
        if (!railTypes.contains(railBlockType)) return;

        Material blockBelowType = event.getVehicle().getLocation().getBlock().getRelative(0, -1, 0).getType();
        double blockMultiplier = _blockMaxSpeeds.getOrDefault(blockBelowType, VANILLA_MAX_SPEED);
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