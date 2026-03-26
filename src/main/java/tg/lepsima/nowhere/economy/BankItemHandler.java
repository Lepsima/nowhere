package tg.lepsima.nowhere.economy;

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

    public ItemStack generateCurrency(int amount, int value) {
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

    private Inventory getInventory(int x, int y, int z) {
        assert WORLD != null;
        Block block = WORLD.getBlockAt(x, y, z);
        return block.getState() instanceof InventoryHolder holder ? holder.getInventory() : null;
    }

    public void giveCurrency(Inventory inventory, int amount) {
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

        var leftover = inventory.addItem(stacks);

        // Drop leftovers
        if (!leftover.isEmpty()) {
            Location location = inventory.getLocation();
            if (location == null) return;

            for (ItemStack item : leftover.values()) {
                assert WORLD != null;
                WORLD.dropItemNaturally(location, item);
            }
        }
    }

    public int removeMaterials(Inventory inventory, Material material, int amount) {
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

    public int removeCurrency(Inventory inventory, int amount) {
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
    public int getCurrencyValue(ItemStack stack) {
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
