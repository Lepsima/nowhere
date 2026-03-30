package me.lepsima.nowhere.commands.bank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.joml.Vector2i;
import org.joml.Vector3i;
import me.lepsima.nowhere.TGCommand;
import me.lepsima.nowhere.economy.Bank;
import me.lepsima.nowhere.economy.ExchangeSiteData;

public class BankTradeCommand extends TGCommand implements CommandExecutor {
    public BankTradeCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equals("reload")) {
            ExchangeSiteData.clearSiteCache();
            return true;
        }

        String material = args[2];
        Vector3i v1 = getCoords(args[3]);
        Vector3i v2 = getCoords(args[4]);
        ExchangeSiteData site = ExchangeSiteData.getSite(args[0], material, v1, v2);

        if (site == null) {
            sender.sendMessage("Invalid parameters for the trade.");
            return true;
        }

        switch (args[1]) {
            case "see-money": {
                Vector2i data = site.getMaterialValue();
                sender.sendMessage("COST: " + data.x);
                sender.sendMessage("RETURN: " + data.y);
                break;
            }

            case "see-material": {
                Vector2i data = site.getCurrencyValue();
                sender.sendMessage("COST: " + data.x);
                sender.sendMessage("RETURN: " + data.y);
                break;
            }

            case "get-money":
                site.tradeMaterialsToCurrency();
                break;

            case "get-material":
                site.tradeCurrencyToMaterials();
                break;
        }

        return true;
    }

    private Vector3i getCoords(String arg) {
        String[] args = arg.split(",");

        return new Vector3i(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Integer.parseInt(args[2])
        );
    }
}