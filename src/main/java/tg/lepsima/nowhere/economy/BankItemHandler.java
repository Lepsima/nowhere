package tg.lepsima.nowhere.economy;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BankItemHandler {
    public static final int MAX_CURRENCY_VALUE = 500;

    public int removeMaterials(Player player, Material material, int amount) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        int remaining = amount;

        for (ItemStack stack : contents) {
            if (remaining <= 0) {
                break;
            }

            if (stack.getType() == material) {
                int stackAmount = stack.getAmount();
                stack.subtract(remaining);
                remaining -= stackAmount;
            }
        }

        return amount - remaining;
    }

    public int removeCurrency(Player player, NamespacedKey key, int amount) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();
        int remaining = amount;

        for (ItemStack stack : contents) {
            if (remaining <= 0) {
                break;
            }

            // Get value of item and amount of items
            int stackAmount = stack.getAmount();
            int itemValue = getCurrencyValue(stack, key);

            // Skip if the value is zero
            if (itemValue == 0) {
                continue;
            }

            // Remove necessary amount of items
            int removeItems = Math.min(Math.ceilDiv(remaining, itemValue), stackAmount);
            remaining -= removeItems * itemValue;
        }

        // Give back remainder if necessary
        if (remaining < 0) {
            int giveAmount = Math.abs(remaining);
            giveCurrency(player, key, giveAmount);
        }

        return amount - remaining;
    }

    public void giveCurrency(Player player, NamespacedKey key, int amount) {
        int remainder = amount % MAX_CURRENCY_VALUE;
        int fullStacks = amount / MAX_CURRENCY_VALUE;


    }

    public int getCurrencyValue(ItemStack stack, NamespacedKey key) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return 0;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        Integer itemValue = container.get(key, PersistentDataType.INTEGER);
        if (itemValue == null) {
            return 0;
        }

        return itemValue;
    }
}
