package tg.lepsima.nowhere.economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashMap;

public class ExchangeSiteData {
    private static final World WORLD = Bukkit.getWorld("world");
    private static final HashMap<Vector3i, ExchangeSiteData> SITES = new HashMap<>();

    public Bank bank;
    public BankResource resource;
    public Inventory inventory;
    public Sign sign;

    public ExchangeSiteData getSite(String bankName, String material, Vector3i v1, Vector3i v2) {
        if (!SITES.containsKey(v1)) {
            SITES.put(v1, new ExchangeSiteData(bankName, material, v1, v2));
        }

        return SITES.get(v1);
    }

    public ExchangeSiteData(String bankName, String material, Vector3i v1, Vector3i v2) {
        bank = Bank.ALL_BANKS.get(bankName);
        resource = bank.getResource(Material.getMaterial(material));

        assert WORLD != null;
        Block inventoryBlock = WORLD.getBlockAt(v1.x, v1.y, v1.z);
        inventory = inventoryBlock.getState() instanceof InventoryHolder holder ? holder.getInventory() : null;

        Block signBlock = WORLD.getBlockAt(v2.x, v2.y, v2.z);
        sign = (Sign)signBlock;
    }

    // Sold item count, Exact currency given
    public Vector2i getMaterialValue() {
        int stock = resource.currentStock;
        int materials = BankItemHandler.countMaterials(inventory, resource.material);
        int value = resource.getSellValueAtRange(stock, materials);
        return new Vector2i(value, materials);
    }

    // Exact budget, Bought item count
    public Vector2i getCurrencyValue() {
        int budget = BankItemHandler.countCurrency(inventory);
        int stock = resource.currentStock;
        return resource.getBuyRoundedBudget(stock, budget);
    }

    public void tradeCurrencyToMaterials() {
        Vector2i data = getCurrencyValue();
        BankItemHandler.removeCurrency(inventory, data.x);
        BankItemHandler.giveMaterials(inventory, resource.material, data.y);
    }

    public void tradeMaterialsToCurrency() {
        Vector2i data = getMaterialValue();
        BankItemHandler.removeMaterials(inventory, resource.material, data.x);
        BankItemHandler.giveCurrency(inventory, data.y);
    }
}
