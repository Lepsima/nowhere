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

import java.util.HashMap;
import java.util.List;

public class BankItemHandler {
    public static final int MAX_CURRENCY_VALUE = 500;
    public static final int MAX_CURRENCY_STACK = 128;
    private static final World WORLD = Bukkit.getWorld("world");
    private static final NamespacedKey CURRENCY_KEY = new NamespacedKey("nowhere", "currency");

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
        meta.getPersistentDataContainer().set(CURRENCY_KEY, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);

        return item;
    }

    public static void giveCurrency(Inventory inventory, int amount) {
        int fullValueItems = amount / MAX_CURRENCY_VALUE;
        int remainder = amount % MAX_CURRENCY_VALUE;

        int fullValueStacks = fullValueItems / MAX_CURRENCY_STACK;
        int remainderStack = fullValueItems % MAX_CURRENCY_STACK;

        int stackCount = fullValueStacks + Math.min(remainderStack, 1) + Math.min(remainder, 1);
        int index = 0;
        ItemStack[] stacks = new ItemStack[stackCount];

        if (fullValueItems != 0) {
            // Generate full stacks of full value papers
            if (fullValueStacks != 0) {
                for (int i = 0; i < fullValueStacks; i++) {
                    stacks[index] = generateCurrency(MAX_CURRENCY_VALUE, MAX_CURRENCY_STACK);
                    index++;
                }
            }

            // Generate incomplete stack of full value papers
            if (remainderStack != 0) {
                stacks[index] = generateCurrency(MAX_CURRENCY_VALUE, remainderStack);
                index++;
            }
        }

        // Add a singular item with the reminder value
        if (remainder != 0) {
            stacks[index] = generateCurrency(remainder, 1);
        }

        giveItems(inventory, stacks);
    }

    public static void giveMaterials(Inventory inventory, Material material, int amount) {
        ItemStack[] stacks = { ItemStack.of(material, amount) };
        giveItems(inventory, stacks);
    }

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

    public static int countMaterials(Inventory inventory, Material material) {
        ItemStack[] contents = inventory.getContents();
        int total = 0;

        for (ItemStack stack : contents) {
            if (stack.getType() == material) {
                total += stack.getAmount();
            }
        }

        return total;
    }

    public static int countCurrency(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        int total = 0;

        for (ItemStack stack : contents) {
            int stackAmount = stack.getAmount();
            int itemValue = getCurrencyValue(stack);
            total += stackAmount * itemValue;
        }

        return total;
    }

    public static int removeMaterials(Inventory inventory, Material material, int amount) {
        ItemStack[] contents = inventory.getContents();
        int remaining = amount;
        int index = 0;

        while (remaining > 0 && index < contents.length) {
            ItemStack stack = contents[index];
            index++;

            if (stack.getType() == material) {
                int stackAmount = stack.getAmount();
                stack.subtract(remaining);
                remaining -= stackAmount;
            }
        }

        return amount - remaining;
    }

    public static int removeCurrency(Inventory inventory, int amount) {
        ItemStack[] contents = inventory.getContents();
        int remaining = amount;
        int index = 0;

        while (remaining > 0 && index < contents.length) {
            ItemStack stack = contents[index];
            index++;

            // Get value of item and amount of items
            int stackAmount = stack.getAmount();
            int itemValue = getCurrencyValue(stack);

            // Remove necessary amount of items if the value is not zero
            if (itemValue != 0) {
                int removeItems = Math.min(Math.ceilDiv(remaining, itemValue), stackAmount);
                remaining -= removeItems * itemValue;
            }
        }

        // Give back remainder if necessary
        if (remaining < 0) {
            int giveAmount = Math.abs(remaining);
            giveCurrency(inventory, giveAmount);
        }

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
