package com.aquobus.antagoncore.modules.villagerTransportation;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Camel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.utils.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VillagerTransportation implements Listener {
    private final Set<UUID> transportationCooldown = new HashSet<>();
    private AntagonCore plugin;
    private final double moveThreshold = 0.1; // порог для определения значительного перемещения
    
    public VillagerTransportation(AntagonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!plugin.isVillagerTransportationEnabled) {
            return;
        }

        Entity vehicle = event.getVehicle();
        
        // Проверка изменения местоположения
        if (event.getFrom().distance(event.getTo()) < moveThreshold) {
            return; // Выход, если движение незначительное
        }

        if (vehicle instanceof Camel) {
            if (vehicle.getPassengers().get(0) instanceof Player && vehicle.getPassengers().size() == 1) {
                handleVehiclePassengerChange(vehicle);
            }
        }

        if (vehicle instanceof Llama) {
            Llama llama = (Llama) vehicle;
            if (llama.isLeashed() && llama.isEmpty()) {
                handleVehiclePassengerChange(vehicle);
            }
        }
    }

    private void handleVehiclePassengerChange(Entity vehicle) {
        Villager nearestVillager = findNearestVillager(vehicle);
        if (nearestVillager != null) { 
            nearestVillager.addPassenger(vehicle);
        }
    }

    private Villager findNearestVillager(Entity vehicle) {
        return vehicle.getWorld().getNearbyEntitiesByType(Villager.class, vehicle.getLocation(), 1.0).stream()
            .filter(entity -> !transportationCooldown.contains(entity.getUniqueId()))
            .findFirst()
            .orElse(null);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity target = event.getRightClicked();
        
        if (target instanceof Villager) {
            Villager villager = (Villager) target;
            if (villager.getVehicle() != null) {
                villager.eject();
                transportationCooldown.add(villager.getUniqueId());
                Utils.scheduleAsync(100, () -> transportationCooldown.remove(villager.getUniqueId()));
            }
        }
    }
}
