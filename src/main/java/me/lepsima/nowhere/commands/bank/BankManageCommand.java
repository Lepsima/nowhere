package me.lepsima.nowhere.commands.bank;

import me.lepsima.nowhere.economy.BankResource;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.lepsima.nowhere.TGCommand;
import me.lepsima.nowhere.economy.Bank;

import java.util.Collection;

public class BankManageCommand extends TGCommand implements CommandExecutor {
    public BankManageCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bank bank = Bank.ALL_BANKS.get(args[0]);
        if (bank == null || !bank.isCorrectPassword(args[1])){
            sender.sendMessage("Incorrect password or name for bank: " + args[0]);
            return true;
        }

        Player player = (Player)sender;
        Material material = null;

        if (!args[2].equals("view-stock") && !args[2].equals("view-cost")) {
            material = Material.getMaterial(args[3]);

            if (material == null) {
                sender.sendMessage("Invalid material");
                return true;
            }
        }

        switch (args[2]) {
            // Make the bank trade a new item
            case "new-resource":
                try {
                    int initialValue = Integer.parseInt(args[4]);
                    int initialStock = Integer.parseInt(args[5]);
                    bank.createResource(material, initialValue, initialStock);

                } catch (Exception e) {
                    sender.sendMessage("Incorrect arguments for adding a new resource");
                    return true;
                }

                break;

            // Remove an item trade from the bank
            case "del-resource":
                bank.deleteResource(material);
                break;

            case "view-stock": {
                Collection<BankResource> resources = bank.getAllResources();
                sender.sendMessage("Resources found: " + resources.size());

                for (BankResource res : resources) {
                    String matName = res.material.toString();
                    int stock = res.initialStock;
                    int value = res.initialValue;
                    int curStock = res.currentStock;

                    String msg = "%s -> Current stock: %s, Initial Stock/Value: %s/%s";
                    sender.sendMessage(String.format(msg, matName, stock, value, curStock));
                }

                break;
            }

            case "view-cost": {
                Collection<BankResource> resources = bank.getAllResources();
                sender.sendMessage("Resources found: " + resources.size());

                for (BankResource res : resources) {
                    String matName = res.material.toString();
                    int stock = res.currentStock;
                    int buyValue =  (int)Math.ceil(res.getBuyValueAt(stock));
                    int sellValue = (int)Math.ceil(res.getSellValueAt(stock));

                    int value = Math.max(buyValue, sellValue);
                    sender.sendMessage(String.format("%s -> Value: %s, Stock: %s", matName, value, stock));
                }

                break;
            }

            // restock a bank resource (used by the owner)
            case "add-stock": {
                if (player == null) {
                    return true;
                }

                try {
                    int amount = Integer.parseInt(args[4]);
                    BankResource resource = bank.getResource(material);
                    resource.addStock(player.getInventory(), amount);

                } catch (Exception e) {
                    sender.sendMessage("Invalid amount " + e);
                    return true;
                }

                break;
            }

            // get stock back from a bank resource (used by the owner)
            case "remove-stock": {
                if (player == null) {
                    return true;
                }

                try {
                    int amount = Integer.parseInt(args[4]);
                    BankResource resource = bank.getResource(material);
                    resource.removeStock(player.getInventory(), amount);

                } catch (Exception e) {
                    sender.sendMessage("Invalid amount " + e);
                    return true;
                }

                break;
            }
        }

        return true;
    }
}