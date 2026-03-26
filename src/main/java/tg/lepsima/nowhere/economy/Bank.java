package tg.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import tg.lepsima.nowhere.Main;

import java.util.*;

public class Bank implements ConfigurationSerializable {
    public final static String RESOURCE_PATH = "banks.yml";

    private final JavaPlugin plugin;
    private final String name;

    private String password;
    private double interest;

    private final HashMap<Material, BankResource> resources = new HashMap<>();

    public Bank(JavaPlugin plugin, String name, String password, double interest, List<String> resources) {
        this(plugin, name);

        this.password = password;
        this.interest = interest;

        for (String str : resources) {
            BankResource resource = BankResource.fromString(str);
            this.resources.put(resource.material, resource);
        }
    }

    public Bank(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return "BANK_" + getName();
    }

    public List<String> getResourcesAsString() {
        List<String> list = new ArrayList<>();
        for (BankResource res : resources.values()) {
            list.add(res.toString());
        }
        return list;
    }

    public @NonNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        List<String> resourceList = getResourcesAsString();

        data.put("name", this.name);
        data.put("password", this.password);
        data.put("interest", this.interest);
        data.put("resources", resourceList);

        return data;
    }

    public static Bank deserialize(Map<String, Object> args) {
        JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

        String name = (String)args.get("name");
        String password = (String)args.get("password");
        double interest = (double)args.get("interest");
        List<String> resourceList = (List<String>)args.get("resources");

        return new Bank(plugin, name, password, interest, resourceList);
    }

    public void createMaterial(Material material, int initialValue, int initialStock) {
        resources.put(material, new BankResource(material, initialValue, initialStock));
    }

    public void deleteMaterial(Material material) {
        resources.remove(material);
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

}
