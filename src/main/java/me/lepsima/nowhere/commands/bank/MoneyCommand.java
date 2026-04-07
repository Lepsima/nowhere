package me.lepsima.nowhere.commands.bank;

import me.lepsima.nowhere.TGCommand;
import me.lepsima.nowhere.economy.BankItemHandler;
import me.lepsima.nowhere.economy.ExchangeSiteData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class MoneyCommand extends TGCommand implements CommandExecutor {
    public MoneyCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        if (args[0].equals("merge")) {
            GroupMoney(player, Integer.MAX_VALUE);
            return true;
        }

        if (args[0].equals("group")) {
            try {
                GroupMoney(player, Integer.parseInt(args[1]));

            } catch (Exception e) {
                sender.sendMessage("Invalid amount to group, MIN: 0, MAX: Your inventory value");
            }
        }

        return true;
    }

    private void GroupMoney(Player player, int maxAmount) {
        Inventory inv = player.getInventory();
        int inventoryValue = Integer.max(BankItemHandler.countCurrency(inv), maxAmount);

        BankItemHandler.removeCurrency(inv, inventoryValue);
        BankItemHandler.giveCurrency(inv, inventoryValue);
    }
}