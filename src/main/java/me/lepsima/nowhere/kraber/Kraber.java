package me.lepsima.nowhere.kraber;

import me.lepsima.nowhere.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Kraber {
    private final Map<UUID, KraberData> playerData = new HashMap<>();
    private final Cooldown cooldown = new Cooldown(5000);

    private final static int BUDGET = 350;
    private final static int PLAYER_BUDGET = 100;
    private final static int MAX_PLAYERS = 4;

    private int currentPlayerCount;
    private float currentViewDistance;

    public void setPlayerCount(int players) {
        if (players == currentPlayerCount) return;
        currentPlayerCount = players;
        currentViewDistance = getPlayerViewDistance(players);
    }

    public int getChunkBudget(int players) {
        int budget = BUDGET + PLAYER_BUDGET * Math.min(players, MAX_PLAYERS);
        return Math.clamp(budget, 1, 1000);
    }

    public float getPlayerViewDistance(int players) {
        float budget = getChunkBudget(players);
        return (float)(Math.sqrt(budget / players) - 1) / 2F;
    }

    public float getPlayerSimDistance(float viewDistance) {
        return Math.clamp(viewDistance, 2, 6);
    }

    public float getVelocityModifier(double speed) {
        if (speed < 4) return 1F;
        else if (speed < 10) return 0.75F;
        else if (speed < 35) return 0.5F;
        else return 0.25F;
    }

    public void handlePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        KraberData data = playerData.putIfAbsent(uuid, new KraberData());

        if (data == null) return;
        data.recordSpeed(player, 0.5F);

        if (!cooldown.isAvailable(uuid)) return;
        cooldown.startCooldown(uuid);

        float viewDistance = currentViewDistance;

        if (data.isVelocityValid()) {
            double speed = data.getAverageSpeed();
            float modifier = getVelocityModifier(speed);
            viewDistance *= modifier;
        }

        float simDistance = getPlayerSimDistance(viewDistance);
        data.setDistances(player, viewDistance, simDistance);
    }
}
