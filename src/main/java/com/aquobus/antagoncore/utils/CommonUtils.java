package com.aquobus.antagoncore.utils;

import org.bukkit.Location;
import org.bukkit.Material;

public class CommonUtils {
    public void checkNearBlock(Location nullpointLocation, double radius, Material material) {
        for (double x = nullpointLocation.getX() - radius; x <= nullpointLocation.getX() + radius; x++) {
            for (double y = nullpointLocation.getY() - radius; x <= nullpointLocation.getY() + radius; y++) {
                for (double z = nullpointLocation.getZ() - radius; x <= nullpointLocation.getZ() + radius; z++) {
                }
            }
        }
    }
}