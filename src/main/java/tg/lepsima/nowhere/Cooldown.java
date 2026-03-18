package tg.lepsima.nowhere;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    private final HashMap<UUID, Long> cooldownMap = new HashMap<>();
    private final int time;

    public Cooldown(int milliseconds) {
        time = milliseconds;
    }

    public boolean isAvailable(UUID uuid) {
        if (cooldownMap.containsKey(uuid)) {
            long lastUse = cooldownMap.getOrDefault(uuid, 0L);
            long now = System.currentTimeMillis();
            return (now - lastUse) >= time;
        }

        return true;
    }

    public int getTimeLeft(UUID uuid) {
        long lastUse = cooldownMap.getOrDefault(uuid, 0L);
        long now = System.currentTimeMillis();
        return (int)((time - (now - lastUse)) / 1000);
    }

    public void startCooldown(UUID uuid) {
        cooldownMap.put(uuid, System.currentTimeMillis());
    }
}
