package me.lepsima.nowhere.commands.event;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import me.lepsima.nowhere.TGCommand;

public class TpWorldCommand extends TGCommand implements CommandExecutor {
    public TpWorldCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Error");
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:overworld run tp " + args[0] + " 0 78 0");
        return true;
    }
}