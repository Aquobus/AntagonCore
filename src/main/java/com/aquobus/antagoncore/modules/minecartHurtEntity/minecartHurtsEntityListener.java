package com.aquobus.antagoncore.modules.minecartHurtEntity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;

import com.aquobus.antagoncore.AntagonCore;

public class minecartHurtsEntityListener implements Listener {
    private final AntagonCore plugin;

    public minecartHurtsEntityListener(AntagonCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onMinecartCollideEntityEvent(VehicleEntityCollisionEvent event) {
        int minecartDamage = plugin.config.getInt("minecartDamage");

        if (!(plugin.isMinecartDamageEnabled)) {
            return;
        }

        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();
            Vector velocity = minecart.getVelocity();
            
            // Проверяем, превышает ли скорость 0.3
            if (velocity.length() > 0.3) {
                // Получаем сущность, с которой произошло столкновение
                Entity collidedEntity = event.getEntity();
                
                // Наносим урон
                double damage = velocity.length() * minecartDamage;
                if (collidedEntity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) collidedEntity;
                    livingEntity.damage(damage);
                }
            }
        }
    }
}
