package me.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import me.lepsima.nowhere.Main;

import java.io.File;
import java.io.IOException;
import java.util.*;

// This class represents an individual bank
// Multiple banks can exist, each one with different stock and resources
public class Bank implements ConfigurationSerializable {
    public final static String RESOURCE_PATH = "banks.yml";
    public final static HashMap<String, Bank> ALL_BANKS = new HashMap<>();

    private final String name;
    private final String password;
    private final double interest;

    private double balance;

    private final HashMap<Material, BankResource> resources = new HashMap<>();

    // Create new bank instance
    public Bank(String name, String password, double interest, double balance, List<String> resources) {
        this.name = name;
        this.password = password;
        this.interest = interest;
        this.balance = balance;

        // Import resources, optional
        for (String str : resources) {
            BankResource resource = BankResource.fromString(this, str);
            this.resources.put(resource.material, resource);
        }
    }

    // Checks if the password is correct
    public boolean isCorrectPassword(String password) {
        return this.password.equals(password);
    }

    // Get the banks save file
    private static File getBankFile() {
        JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);
        return new File(plugin.getDataFolder(), Bank.RESOURCE_PATH);
    }

    // Load all bank instances from the file
    @SuppressWarnings("unchecked")
    public static void loadBanks() {

        // Get yml
        ALL_BANKS.clear();
        File bankFile = getBankFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        // Convert yml to bank instances
        List<Map<?, ?>> mapList = config.getMapList("banks");
        for (Map<?, ?> mapEntry : mapList) {
            Bank bank = Bank.deserialize((Map<String, Object>)mapEntry);
            ALL_BANKS.put(bank.name, bank);
        }
    }

    // Save banks to file
    public static void saveBanks() {
        File bankFile = getBankFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        // Banks to <Name, Bank> map for the yml to handle
        List<Map<String, Object>> mapList = ALL_BANKS.values().stream().map(Bank::serialize).toList();
        config.set("banks", mapList);

        // Try to save the file
        try {
            config.save(bankFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Convert this bank instance to valid YML format
    public @NonNull Map<String, Object> serialize() {
        List<String> resourceList = new ArrayList<>();
        for (BankResource res : resources.values()) {
            resourceList.add(res.toString());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", this.name);
        data.put("password", this.password);
        data.put("interest", this.interest);
        data.put("balance", this.balance);
        data.put("resources", resourceList);

        return data;
    }

    // Create a new bank from valid YML data
    @SuppressWarnings("unchecked")
    public static Bank deserialize(Map<String, Object> args) {
        String name = (String)args.get("name");
        String password = (String)args.get("password");
        double interest = (double)args.get("interest");
        double balance = (double)args.get("balance");
        List<String> resourceList = (List<String>)args.get("resources");

        return new Bank(name, password, interest, balance, resourceList);
    }

    public double getBalance() {
        return balance;
    }

    public void changeBalance(double amount) {
        balance += amount;
    }

    public void addBalance(Inventory inventory, int amount) {
        int inventoryValue = BankItemHandler.countCurrency(inventory);
        int budget = Math.min(inventoryValue, amount);

        BankItemHandler.removeCurrency(inventory, budget);
        changeBalance(budget);
    }

    public void removeBalance(Inventory inventory, int amount) {
        int budget = Math.min((int)balance, amount);
        BankItemHandler.giveCurrency(inventory, budget);
        changeBalance(-budget);
    }

    // Create a new resource stock for a new material
    public void createResource(Material material, int initialValue, int initialStock) {
        resources.put(material, new BankResource(this, material, initialValue, initialStock));
    }

    // Delete the bank's stock for this material
    public void deleteResource(Material material) {
        resources.remove(material);
    }

    // Get the resource for a specific material
    public BankResource getResource(Material material) {
        return resources.get(material);
    }

    public Collection<BankResource> getAllResources() {
        return resources.values();
    }
}
