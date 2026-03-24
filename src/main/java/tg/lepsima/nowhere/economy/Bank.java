package tg.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Bank {
    private final JavaPlugin plugin;
    private final String name;
    private final HashMap<Material, BankResource> resources = new HashMap<>();

    public Bank(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.name = name;

        load();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return "BANK_" + getName();
    }

    public void createMaterial(Material material, int initialValue, int initialStock) {
        resources.put(material, new BankResource(material, initialValue, initialStock));
        save();
    }

    public void deleteMaterial(Material material) {
        resources.remove(material);
        save();
    }

    public void tryBuyMaterial(Player player, Material material, int amount) {
        BankResource resource = resources.get(material);
        int playerBalance = 0; // GET THIS VALUE FROM THE PLAYER

        if (playerBalance < amount) {
            player.sendMessage("Insufficient balance to buy.");
            return;
        }
    }

    public void trySellMaterial(Player player, Material material, int amount) {
        BankResource resource = resources.get(material);
        ItemStack playerMaterials = new ItemStack(Material.ACACIA_BOAT, 1); // GET THIS VALUE FROM THE PLAYER

        if (playerMaterials.getAmount() < amount) {
            player.sendMessage("Insufficient items found to sell.");
            return;
        }

    }

    public void load() {
        resources.clear();

        List<String> list = plugin.getConfig().getStringList(getPath());
        for (String str : list) {
            BankResource resource = BankResource.fromString(str);
            resources.put(resource.material, resource);
        }
    }

    public void save() {
        List<String> list = new ArrayList<>();
        for (BankResource res : resources.values()) {
            list.add(res.toString());
        }

        plugin.getConfig().set(getPath(), list);
        plugin.saveConfig();
    }
}
