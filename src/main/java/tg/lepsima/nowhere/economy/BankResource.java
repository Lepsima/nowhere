package tg.lepsima.nowhere.economy;

import org.bukkit.Material;

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
        return (double)initialStock / initialValue * stock;
    }

    public double getSellValueAt(int stock) {
        return getBuyValueAt(stock + 1);
    }

    public double getBuyValueAtRange(int stock, int amount) {
        double cost = 0;
        for (int i = 0; i < amount; i++) {
            cost += getBuyValueAt(stock - i);
        }
        return cost;
    }

    public double getSellValueAtRange(int stock, int amount) {
        double cost = 0;
        for (int i = 0; i < amount; i++) {
            cost += getSellValueAt(stock + i);
        }
        return cost;
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
