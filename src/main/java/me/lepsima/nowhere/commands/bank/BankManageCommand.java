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

        int arg4 = 0;
        int arg5 = 0;

        try {
            if (args.length > 4) arg4 = Integer.parseInt(args[4]);
            if (args.length > 5) arg5 = Integer.parseInt(args[5]);

        } catch (Exception e) {
            sender.sendMessage("Bad value parameters, error parsing");
            return true;
        }

        switch (args[2]) {
            // Make the bank trade a new item
            case "new-resource": {
                Material material = getMaterial(args[3]);
                bank.createResource(material, arg4, arg5);
                break;
            }

            // Remove an item trade from the bank
            case "remove-resource": {
                Material material = getMaterial(args[3]);

                BankResource resource = bank.getResource(material);
                if (resource == null) break;

                // Give the player all the stock
                int stock = resource.currentStock;
                resource.removeStock(player.getInventory(), stock);

                // Remove the now empty resource
                bank.deleteResource(material);
                break;
            }

            case "view-stock": {
                Collection<BankResource> resources = bank.getAllResources();
                sender.sendMessage("Resources found: " + resources.size());

                for (BankResource res : resources) {
                    String matName = res.material.toString();
                    int stock = res.initialStock;
                    int value = res.initialValue;
                    int curStock = res.currentStock;

                    String msg = "%s -> Current stock: %s,   Ideal Stock/Value: %s/%s";
                    sender.sendMessage(String.format(msg, matName, curStock, value, stock));
                }

                break;
            }

            case "view-cost": {
                Collection<BankResource> resources = bank.getAllResources();
                sender.sendMessage("Resources found: " + resources.size());

                for (BankResource res : resources) {
                    String matName = res.material.toString();
                    int stock = res.currentStock;
                    int buyValue = (int) Math.ceil(res.getBuyValueAt(stock));
                    int sellValue = (int) Math.ceil(res.getSellValueAt(stock));

                    int value = Math.max(buyValue, sellValue);
                    sender.sendMessage(String.format("%s -> Value: %s,   Stock: %s", matName, value, stock));
                }

                break;
            }
        }

        if (player == null) {
            return true;
        }

        try {
            switch (args[2]) {
                case "add-balance": {
                    int amount = Integer.parseInt(args[3]);
                    bank.addBalance(player.getInventory(), amount);
                    break;
                }

                case "remove-balance": {
                    int amount = Integer.parseInt(args[3]);
                    bank.removeBalance(player.getInventory(), amount);
                    break;
                }

                // restock a bank resource (used by the owner)
                case "add-stock": {
                    Material material = getMaterial(args[3]);
                    BankResource resource = bank.getResource(material);
                    resource.addStock(player.getInventory(), arg4);
                    break;
                }

                // get stock back from a bank resource (used by the owner)
                case "remove-stock": {
                    Material material = getMaterial(args[3]);
                    BankResource resource = bank.getResource(material);
                    resource.removeStock(player.getInventory(), arg4);
                    break;
                }
            }

        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount " + e);
            return true;
        }


        return true;
    }

    public Material getMaterial(String arg) {
        Material material = Material.getMaterial(arg);

        if (material == null) {
            throw new RuntimeException("Invalid material");
        }

        return material;
    }
}