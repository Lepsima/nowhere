package me.lepsima.nowhere.commands.bank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import me.lepsima.nowhere.TGCommand;
import me.lepsima.nowhere.economy.Bank;

import java.util.ArrayList;

public class BankAdminCommand extends TGCommand implements CommandExecutor {
    public BankAdminCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]) {
            // Create a new bank
            case "create-bank":
                try {
                    String name = args[1];
                    String password = args[2];
                    double interest = Double.parseDouble(args[3]);

                    if (Bank.ALL_BANKS.containsKey(name)) {
                        throw new Exception();
                    }

                    Bank bank = new Bank(name, password, interest, new ArrayList<>());
                    Bank.ALL_BANKS.put(name, bank);
                    Bank.saveBanks();

                } catch (Exception e) {
                    sender.sendMessage("Incorrect arguments for creating a bank");
                }
                break;

            // Delete a bank
            case "delete-bank":
                Bank bank = Bank.ALL_BANKS.get(args[1]);
                if (bank != null && bank.isCorrectPassword(args[2])) {
                    Bank.ALL_BANKS.remove(args[1]);
                    Bank.saveBanks();
                }
                break;

            case "save-banks":
                Bank.saveBanks();
                break;

            case "load-banks":
                Bank.loadBanks();
                break;
        }

        return true;
    }
}