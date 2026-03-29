package me.lepsima.nowhere.commands.bank;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.lepsima.nowhere.TGCommand;
import me.lepsima.nowhere.economy.Bank;

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
        Material material = Material.getMaterial(args[3]);

        if (material == null) {
            sender.sendMessage("Invalid material");
            return true;
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


            // restock a bank resource (used by the owner)
            case "add-stock":
                if (player != null) {
                    //BankResource resource = bank.getResource()
                }

                break;

            // get stock back from a bank resource (used by the owner)
            case "remove-stock":
                if (player != null) {

                }
                break;
        }

        return true;
    }
}