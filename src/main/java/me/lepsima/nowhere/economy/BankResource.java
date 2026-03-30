package me.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.joml.Vector2i;

// This keeps track of how much of a certain material a bank has
// It provides prices and return count at many stock amounts
public class BankResource {
    public final Material material;
    public final int initialValue;
    public final int initialStock;
    public int currentStock;

    public BankResource(Material material, int initialValue, int initialStock) {
        this(material, initialValue, initialStock, 0);
    }

    public BankResource(Material material, int initialValue, int initialStock, int currentStock) {
        this.material = material;
        this.initialValue = initialValue;
        this.initialStock = initialStock;
        this.currentStock = currentStock;
    }

    public void addStock(int amount) {
        currentStock += amount;
    }

    public void removeStock(int amount) {
        currentStock -= amount;
        currentStock = Math.max(0, currentStock);
    }

    // For owner use, add new materials to the stock
    public void addStock(Inventory from, int amount) {
        int materials = BankItemHandler.countMaterials(from, material);
        int realAmount = Math.min(amount, materials);

        BankItemHandler.removeMaterials(from, material, realAmount);
        addStock(realAmount);
    }

    // For owner use, get back materials from the stock
    public  void removeStock(Inventory from, int amount) {
        int realAmount = Math.min(amount, currentStock);
        BankItemHandler.giveMaterials(from, material, realAmount);
        removeStock(realAmount);
    }

    // How much it would cost to buy 1 material at "stock" amount of stock
    public double getBuyValueAt(int stock) {
        if (stock < 1) return Integer.MAX_VALUE;
        return (double)initialStock / stock * initialValue;
    }

    // How much you would get when selling 1 material at "stock" amount of stock
    public double getSellValueAt(int stock) {
        if (stock < 1) stock = 0;
        return getBuyValueAt(stock + 1);
    }

    // Buy "amount" times this material, starting at "stock" amount of stock
    // Returns: 2 ints (Exact cost needed, Amount bought)
    public Vector2i getBuyRoundedBudget(int stock, double budget) {
        int amount = 0;
        double remainingBudget = budget;

        while (true) {
            double value = getBuyValueAt(stock - amount);
            if (value > remainingBudget) break;

            remainingBudget -= value;
            amount++;
        }

        return new Vector2i((int)Math.ceil(budget - remainingBudget), amount);
    }

    // Sell "amount" times this material, starting at "stock" amount of stock
    // Returns: 2 ints (Amount sold, money given for it)
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
