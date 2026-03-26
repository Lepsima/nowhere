package tg.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import tg.lepsima.nowhere.Main;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Bank implements ConfigurationSerializable {
    public final static String RESOURCE_PATH = "banks.yml";
    public final static List<Bank> ALL_BANKS = new ArrayList<>();

    private final String name;
    private final String password;
    private final double interest;

    private final HashMap<Material, BankResource> resources = new HashMap<>();

    public Bank(String name, String password, double interest, List<String> resources) {
        this.name = name;
        this.password = password;
        this.interest = interest;

        for (String str : resources) {
            BankResource resource = BankResource.fromString(str);
            this.resources.put(resource.material, resource);
        }
    }

    private static File getBankFile() {
        JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);
        return new File(plugin.getDataFolder(), Bank.RESOURCE_PATH);
    }

    @SuppressWarnings("unchecked")
    public static void loadBanks() {
        File bankFile = getBankFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        List<Map<?, ?>> mapList = config.getMapList("banks");
        for (Map<?, ?> mapEntry : mapList) {
            Map<String, Object> map = (Map<String, Object>) mapEntry;
            ALL_BANKS.add(Bank.deserialize(map));
        }
    }

    public static void saveBanks() {
        File bankFile = getBankFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        List<Map<String, Object>> mapList = ALL_BANKS.stream().map(Bank::serialize).toList();
        config.set("banks", mapList);

        try {
            config.save(bankFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @NonNull Map<String, Object> serialize() {
        List<String> resourceList = new ArrayList<>();
        for (BankResource res : resources.values()) {
            resourceList.add(res.toString());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", this.name);
        data.put("password", this.password);
        data.put("interest", this.interest);
        data.put("resources", resourceList);

        return data;
    }

    @SuppressWarnings("unchecked")
    public static Bank deserialize(Map<String, Object> args) {
        String name = (String)args.get("name");
        String password = (String)args.get("password");
        double interest = (double)args.get("interest");
        List<String> resourceList = (List<String>)args.get("resources");

        return new Bank(name, password, interest, resourceList);
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
