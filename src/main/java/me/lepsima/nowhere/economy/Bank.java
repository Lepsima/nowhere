package me.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import me.lepsima.nowhere.Main;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Bank implements ConfigurationSerializable {
    public final static String RESOURCE_PATH = "banks.yml";
    public final static HashMap<String, Bank> ALL_BANKS = new HashMap<>();

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

    public boolean isCorrectPassword(String password) {
        return this.password.equals(password);
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
            Bank bank = Bank.deserialize((Map<String, Object>)mapEntry);
            ALL_BANKS.put(bank.name, bank);
        }
    }

    public static void saveBanks() {
        File bankFile = getBankFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        List<Map<String, Object>> mapList = ALL_BANKS.values().stream().map(Bank::serialize).toList();
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

    public void createResource(Material material, int initialValue, int initialStock) {
        resources.put(material, new BankResource(material, initialValue, initialStock));
    }

    public void deleteResource(Material material) {
        resources.remove(material);
    }

    public BankResource getResource(Material material) {
        return resources.get(material);
    }
}
