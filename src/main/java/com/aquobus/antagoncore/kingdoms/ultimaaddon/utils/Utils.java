package com.aquobus.antagoncore.kingdoms.ultimaaddon.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {
    public static String convertAmps(String s) {
        return s.replaceAll("&", "§");
    }

    public static Component toComponent(String line) {
        return LegacyComponentSerializer.legacySection().deserialize(convertAmps(line)).decoration(TextDecoration.ITALIC, false);
    }
}
