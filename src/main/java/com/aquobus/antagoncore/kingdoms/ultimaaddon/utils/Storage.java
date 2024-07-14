package com.aquobus.antagoncore.kingdoms.ultimaaddon.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.kingdoms.constants.group.Kingdom;

import java.io.File;
import java.io.IOException;

import static com.aquobus.antagoncore.AntagonCore.plugin;

public class Storage {

    /**
     * Возвращает объект File королевства
     * @param kingdom Королевство
     */
    private static File getKingdomFile(Kingdom kingdom) {
        return new File(Bukkit.getPluginsFolder().getAbsolutePath(),"/storage/kingdoms/" + kingdom.getId() + ".yml");
    }

    /**
     * Создаёт yaml хранилище для данных королевства
     * @param kingdom Королевство куда нужно записать данные
     */
    public static void saveKingdomFile(Kingdom kingdom) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.save(getKingdomFile(kingdom));
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка при сохранении файла королевства: " + e.getMessage());
        }
    }

    /**
     * Записывает данные в yaml хранилище королевства |
     * и создаёт yaml хранилище при отсутствии такого |
     * @param kingdom Королевство куда нужно записать |
     * @param key Путь куда записать данные           | discord.RoleID | discord.RoleColor
     * @param value Данные                            | 123456789      | #FFFFFF
     */
    public static void writeToKingdomFile(Kingdom kingdom, String key, Object value) {
        File file = getKingdomFile(kingdom);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(key, value);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка при записи данных в файл королевства: " + e.getMessage());
        }
    }
}

