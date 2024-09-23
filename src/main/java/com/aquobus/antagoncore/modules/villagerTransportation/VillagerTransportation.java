package com.aquobus.antagoncore.modules.villagerTransportation;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Llama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Camel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.aquobus.antagoncore.AntagonCore;
import com.aquobus.antagoncore.modules.kingdoms.ultimaaddon.utils.Utils;

import io.papermc.paper.event.entity.EntityMoveEvent;

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
    public void onVehicleMove(EntityMountEvent event) {
        if (!plugin.isVillagerTransportationEnabled) {
            return;
        }

        Entity vehicle = event.getEntity();
        
        // Проверка изменения местоположения
        // if (event.getFrom().distance(event.getTo()) < moveThreshold) {
        //     return; // Выход, если движение незначительное
        // }

        if (vehicle instanceof Camel) {
            Camel camel = (Camel) vehicle;
            if (camel.getPassengers().get(0) instanceof Player && camel.getPassengers().size() == 1) {
                handleVehiclePassengerChange(camel);
            }
        }

        if (vehicle instanceof Llama) {
            Llama llama = (Llama) vehicle;
            if (llama.isLeashed() && llama.isEmpty()) {
                handleVehiclePassengerChange(llama);
            }
        }
    }

    private void handleVehiclePassengerChange(Entity vehicle) {
        Villager nearestVillager = findNearestVillager(vehicle);
        if (nearestVillager != null) { 
            nearestVillager.addPassenger(vehicle);
            Bukkit.getLogger().info("ближайший житель подобрался!");
        }
    }

    private Villager findNearestVillager(Entity vehicle) {
        Villager villager = vehicle.getWorld().getNearbyEntitiesByType(Villager.class, vehicle.getLocation(), 1.0).stream()
            .filter(entity -> !transportationCooldown.contains(entity.getUniqueId()))
            .findFirst()
            .orElse(null);

        Bukkit.getLogger().info("ближайший житель нашёлся!");

        return villager;
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

                Bukkit.getLogger().info("житель посадился!");
            }
        }
    }
}
