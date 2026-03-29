package me.lepsima.nowhere.kraber;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Kraber {
    Map<UUID, KraberData> playerData = new HashMap<>();

    private final static float VIEW_DISTANCE = 12;
    private final static float SIM_DISTANCE = 4;

    private float playerCountModifier;

    public void setPlayerCount(int count) {
        playerCountModifier = switch (count) {
            case 1 -> 1F;
            case 2 -> 0.8F;
            case 3 -> 0.75F;
            default -> 0.5F;
        };
    }

    public float getVelocityModifier(double speed) {
        if (speed < 4) return 1F;
        else if (speed < 10) return 0.75F;
        else if (speed < 35) return 0.5F;
        else return 0.25F;
    }

    public void handlePlayer(Player player) {
        KraberData data = playerData.get(player.getUniqueId());
        data.recordSpeed(player, 1.0);

        float viewDistance = VIEW_DISTANCE * playerCountModifier;
        float simDistance = SIM_DISTANCE;

        if (data.isVelocityValid()) {
            double speed = data.getAverageSpeed();
            float modifier = getVelocityModifier(speed);

            viewDistance *= modifier;
            simDistance *= modifier;
        }

        data.setDistances(player, viewDistance, simDistance);
    }
}
