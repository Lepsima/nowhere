package tg.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Bank {
    private final JavaPlugin plugin;
    private final String name;
    private String password;
    private final HashMap<Material, BankResource> resources = new HashMap<>();

    public Bank(JavaPlugin plugin, String name, String password) {
        this.plugin = plugin;
        this.name = name;
        this.plugin.saveDefaultConfig();

        changePassword(this.password, password);
        load();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return "BANK_" + getName();
    }

    public String getPasswordPath() {
        return "PASSWORD_" + getPath();
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(password)) {
            password = newPassword;
            save();
        }
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

        FileConfiguration config = plugin.getConfig();
        password = config.getString(getPasswordPath());
        List<String> list = config.getStringList(getPath());

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

        FileConfiguration config = plugin.getConfig();
        config.set(getPath(), list);
        config.set(getPasswordPath(), password);

        plugin.saveConfig();
    }
}
