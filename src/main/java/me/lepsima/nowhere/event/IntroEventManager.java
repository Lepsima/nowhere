package me.lepsima.nowhere.event;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IntroEventManager {
    private static JavaPlugin plugin;
    private static final String PATH = "intro-event";
    private static final Set<UUID> cache = new HashSet<>();

    public static void init(JavaPlugin pl) {
        plugin = pl;
        plugin.saveDefaultConfig();

        load();
    }

    public static void load() {
        cache.clear();

        List<String> list = plugin.getConfig().getStringList(PATH);

        for (String s : list) {
            try {
                cache.add(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("UUID inválido en config: " + s);
            }
        }
    }

    public static void save() {
        List<String> list = cache.stream()
                .map(UUID::toString)
                .toList();

        plugin.getConfig().set(PATH, list);
        plugin.saveConfig();
    }

    public static void addPlayer(UUID uuid) {
        if (cache.add(uuid)) {
            save();
        }
    }

    public static void removePlayer(UUID uuid) {
        if (cache.remove(uuid)) {
            save();
        }
    }

    public static boolean isInList(UUID uuid) {
        return cache.contains(uuid);
    }
}