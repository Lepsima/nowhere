package me.lepsima.nowhere.economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashMap;

// Temporal class specific for ATMs
// These sites are created when commands execute trade orders
// They have an inventory block where they can access and deposit trade materials
public class ExchangeSiteData {
    private static final World WORLD = Bukkit.getWorld("world");
    private static final HashMap<Vector3i, ExchangeSiteData> SITES = new HashMap<>();

    public Bank bank;
    public BankResource resource;
    public Inventory inventory;
    public Sign sign;

    // Get the ATM at these coordinates
    // If it's not cached, create a new one, it's temporal anyway
    public static ExchangeSiteData getSite(String bankName, String material, Vector3i v1, Vector3i v2) {
        if (!SITES.containsKey(v1)) {
            SITES.put(v1, new ExchangeSiteData(bankName, material, v1, v2));
        }

        return SITES.get(v1);
    }

    // Clear cache
    public static void clearSiteCache() {
        SITES.clear();
    }

    // Create new ATM site
    public ExchangeSiteData(String bankName, String material, Vector3i v1, Vector3i v2) {
        bank = Bank.ALL_BANKS.get(bankName);
        resource = bank.getResource(Material.getMaterial(material));

        assert WORLD != null;
        Block inventoryBlock = WORLD.getBlockAt(v1.x, v1.y, v1.z);
        inventory = inventoryBlock.getState() instanceof InventoryHolder holder ? holder.getInventory() : null;

        Block signBlock = WORLD.getBlockAt(v2.x, v2.y, v2.z);
        sign = (Sign)signBlock.getState();
    }

    // Returns how much money the bank gives you for your items, and how many items you can actually sell
    // Returns: (Sold item count, Exact currency given)
    public Vector2i getMaterialValue() {
        int stock = resource.currentStock;
        int materials = BankItemHandler.countMaterials(inventory, resource.material);
        int value = resource.getSellValueAtRange(stock, materials);
        return new Vector2i(materials, value);
    }

    // Returns how many materials the bank gives you, and exactly how much they will cost (to calculate reminder)
    // Returns: (Exact budget needed, Bought item count)
    public Vector2i getCurrencyValue() {
        int budget = BankItemHandler.countCurrency(inventory);
        int stock = resource.currentStock;
        return resource.getBuyRoundedBudget(stock, budget);
    }

    // Trades all money in ATM to materials
    public void tradeCurrencyToMaterials() {
        // Calculate prices and amounts
        Vector2i data = getCurrencyValue();

        // Remove money
        BankItemHandler.removeCurrency(inventory, data.x);

        // Give materials
        BankItemHandler.giveMaterials(inventory, resource.material, data.y);
        resource.removeStock(data.y);
    }

    // Trades all materials in the ATM to money
    public void tradeMaterialsToCurrency() {
        // Calculate prices and amounts
        Vector2i data = getMaterialValue();

        // Remove materials
        BankItemHandler.removeMaterials(inventory, resource.material, data.x);
        resource.addStock(data.x);

        // Give money
        BankItemHandler.giveCurrency(inventory, data.y);
    }
}
