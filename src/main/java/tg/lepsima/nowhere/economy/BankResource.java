package tg.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class BankResource {
    public final Material material;
    public final int initialValue;
    public final int initialStock;
    public int currentStock;

    public BankResource(Material material, int initialValue, int initialStock) {
        this(material, initialValue, initialStock, initialStock);
    }

    public BankResource(Material material, int initialValue, int initialStock, int currentStock) {
        this.material = material;
        this.initialValue = initialValue;
        this.initialStock = initialStock;
        this.currentStock = currentStock;
    }

    public double getBuyValueAt(int stock) {
        if (stock < 1) return Integer.MAX_VALUE;
        return (double)initialStock / initialValue * stock;
    }

    public double getSellValueAt(int stock) {
        if (stock < 1) stock = 0;
        return getBuyValueAt(stock + 1);
    }

    public int getBuyValueAtRange(int stock, int amount) {
        double cost = 0;
        for (int i = 0; i < amount; i++) {
            cost += getBuyValueAt(stock - i);
        }
        return (int)Math.ceil(cost);
    }

    public Vector2i getBuyRoundedBudget(int stock, double budget) {
        int amount = 0;
        double remainingBudget = budget;

        while (true) {
            double value = getBuyValueAt(stock - amount);
            if (value > remainingBudget) break;
            remainingBudget -= value;
        }

        return new Vector2i((int)Math.ceil(budget - remainingBudget), amount);
    }

    public int getSellValueAtRange(int stock, int amount) {
        double cost = 0;
        for (int i = 0; i < amount; i++) {
            cost += getSellValueAt(stock + i);
        }
        return (int)Math.ceil(cost);
    }

    public String toString() {
        return material.name() + "," + initialValue + "," + initialStock + "," + currentStock;
    }

    public static BankResource fromString(String str) {
        String[] parts = str.split(",");
        return new BankResource(
                Material.getMaterial(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
        );
    }
}
