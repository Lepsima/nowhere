package tg.lepsima.nowhere.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tg.lepsima.nowhere.TGCommand;

public class TpNowhereCommand extends TGCommand {
    public TpNowhereCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Error");
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in nowhere:nowhere run tp " + args[0] + " 240 33 0");
        return true;
    }
}