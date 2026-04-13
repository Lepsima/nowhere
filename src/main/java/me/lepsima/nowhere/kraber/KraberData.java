package me.lepsima.nowhere.kraber;

import me.lepsima.nowhere.Cooldown;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class KraberData {
    public static final int VEL_LIST_SIZE = 10;

    private final List<Double> velocityHistory = new ArrayList<>();
    private Location lastLocation;

    private int lastViewDistance;
    private int lastSimDistance;

    public void setDistances(Player player, float view, float sim) {
        sim = Math.min(view, sim);

        int newViewDistance = Math.round(view);
        int newSimDistance = Math.round(sim);

        if (lastViewDistance != newViewDistance) {
            lastViewDistance = newViewDistance;
            player.setViewDistance(newViewDistance);
        }

        if (lastSimDistance != newSimDistance) {
            lastSimDistance = newSimDistance;
            player.setSimulationDistance(newSimDistance);
        }
    }

    public void recordSpeed(Player player, double time) {
        double speed = calculateSpeed(player, time);

        while (velocityHistory.size() >= VEL_LIST_SIZE) {
            velocityHistory.removeFirst();
        }

        velocityHistory.add(speed);
    }

    public boolean isVelocityValid() {
        return velocityHistory.size() >= VEL_LIST_SIZE;
    }

    public double getAverageSpeed() {
        double total = 0;

        for (double vel : velocityHistory) {
            total += vel;
        }

        return total / velocityHistory.size();
    }

    public double calculateSpeed(Player player, double time) {
        Location last = lastLocation;
        Location now = player.getLocation();

        lastLocation = now;

        if (last == null) return 0;
        return last.distance(now) / time;
    }
}
