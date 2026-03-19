package tg.lepsima.nowhere;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntroEvent {
    private static final PersistentDataContainer CONTAINER = getContainer();
    private static final NamespacedKey KEY = new NamespacedKey(Nowhere.Plugin, "intro-event");

    public static PersistentDataContainer getContainer() {
        World world = Bukkit.getServer().getWorld(Nowhere.DIMENSION);
       return Objects.requireNonNull(world).getPersistentDataContainer();
    }

    public static void setPlayer(String player, boolean isComplete) {
        List<String> players = CONTAINER.get(KEY, PersistentDataType.LIST.strings());

        if (players == null) {
            players = new ArrayList<>();
        } else if (players.contains(player) == isComplete) {
            return;
        }

        if (isComplete) {
            players.add(player);
        } else {
            players.remove(player);
        }

        CONTAINER.set(KEY, PersistentDataType.LIST.strings(), players);
    }

    public static boolean isCompleteBy(String player) {
        List<String> players = CONTAINER.get(KEY, PersistentDataType.LIST.strings());
        return Objects.requireNonNull(players).contains(player);
    }
}
