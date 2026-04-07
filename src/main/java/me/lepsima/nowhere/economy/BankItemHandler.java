package me.lepsima.nowhere.economy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// This boi removes and gives items, he knows nothing about banks
// It removes and gives/creates items on demand, on any inventory
public class BankItemHandler {
    public static final int MAX_CURRENCY_VALUE = 1000;
    public static final int MAX_CURRENCY_STACK = 99;
    private static final World WORLD = Bukkit.getWorld("world");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey("nowhere", "currency");

    // Make new money item
    public static ItemStack generateCurrency(int amount, int value) {
        ItemStack item = ItemStack.of(Material.PAPER, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setMaxStackSize(MAX_CURRENCY_STACK);

        // Set name
        String name = "[" + value + "] feelys";
        meta.displayName(Component.text(name)
                .decoration(TextDecoration.BOLD, false));

        // Set lore
        String description = "worth " + value + " feelys";
        meta.lore(List.of(
                Component.text("", NamedTextColor.DARK_PURPLE),
                Component.text(description, NamedTextColor.DARK_PURPLE)
        ));

        // Set custom tag
        meta.getPersistentDataContainer().set(CURRENCY_KEY, PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);

        return item;
    }

    // Give money to an inventory
    public static void giveCurrency(Inventory inventory, int amount) {
        int fullValueItems = amount / MAX_CURRENCY_VALUE; // Amount of maxvalue items
        int remainder = amount % MAX_CURRENCY_VALUE;      // Money remainder

        if (fullValueItems != 0) {
            giveItems(inventory, new ItemStack[] { generateCurrency(fullValueItems, MAX_CURRENCY_VALUE) });
        }

        if (remainder != 0) {
            giveItems(inventory, new ItemStack[] { generateCurrency(1, remainder) });
        }
    }

    // Give resources to an inventory
    public static void giveMaterials(Inventory inventory, Material material, int amount) {
        ItemStack[] stacks = { ItemStack.of(material, amount) };
        giveItems(inventory, stacks);
    }

    // Give specified items to an inventory
    public static void giveItems(Inventory inventory, ItemStack[] stacks) {
        HashMap<Integer, ItemStack> leftover = inventory.addItem(stacks);
        if (leftover.isEmpty()) {
            return;
        }

        Location location = inventory.getLocation();
        if (location == null) return;

        // Drop leftovers
        for (ItemStack item : leftover.values()) {
            assert WORLD != null;
            WORLD.dropItemNaturally(location, item);
        }
    }

    // How many items has this inventory?
    public static int countMaterials(Inventory inventory, Material material) {
        ItemStack[] contents = inventory.getContents();
        int total = 0;

        for (ItemStack stack : contents) {
            if (stack != null && stack.getType() == material) {
                total += stack.getAmount();
            }
        }

        return total;
    }

    // How much money has this inventory?
    public static int countCurrency(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        int total = 0;

        for (ItemStack stack : contents) {
            if (stack == null) {
                continue;
            }

            int stackAmount = stack.getAmount();
            int itemValue = getCurrencyValue(stack);
            total += stackAmount * itemValue;
        }

        return total;
    }

    // Remove this amount of items from this inventory
    public static int removeMaterials(Inventory inventory, Material material, int amount) {
        ItemStack[] contents = inventory.getContents();
        int remaining = amount;
        int index = 0;

        while (remaining > 0 && index < contents.length) {
            ItemStack stack = contents[index];
            index++;

            if (stack != null && stack.getType() == material) {
                int stackAmount = stack.getAmount();
                stack.subtract(remaining);
                remaining -= stackAmount;
            }
        }

        // This should be zero if you previously
        // calculated how many materials has the inventory
        // In any case it tells you how much wasn't able to remove
        return amount - remaining;
    }

    // Remove this amount of money from this inventory
    public static int removeCurrency(Inventory inventory, int amount) {
        ItemStack[] contents = inventory.getContents();
        int remaining = amount;
        int index = 0;

        while (remaining > 0 && index < contents.length) {
            ItemStack stack = contents[index];
            index++;

            if (stack == null) {
                continue;
            }

            // Get value of item and amount of items
            int stackAmount = stack.getAmount();
            int itemValue = getCurrencyValue(stack);

            // Remove necessary amount of items if the value is not zero
            if (itemValue != 0) {
                int removeItems = Math.min(Math.ceilDiv(remaining, itemValue), stackAmount);
                stack.subtract(removeItems);
                remaining -= removeItems * itemValue;
            }
        }

        // Give back remainder if necessary
        if (remaining < 0) {
            int giveAmount = Math.abs(remaining);
            giveCurrency(inventory, giveAmount);
        }

        // This should be zero if you previously
        // calculated how much money has the inventory
        // In any case it tells you how much wasn't able to remove
        return amount - remaining;
    }

    // Gets the value in currency of any item stack
    private static int getCurrencyValue(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return 0;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        Integer itemValue = container.get(CURRENCY_KEY, PersistentDataType.INTEGER);
        if (itemValue == null) {
            return 0;
        }

        return itemValue;
    }
}
