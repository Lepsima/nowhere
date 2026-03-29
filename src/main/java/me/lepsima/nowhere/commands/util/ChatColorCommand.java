package me.lepsima.nowhere.commands.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.lepsima.nowhere.TGCommand;

public class ChatColorCommand extends TGCommand implements CommandExecutor {
    public ChatColorCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        if (args.length != 1 || args[0].length() != 7) {
            sender.sendMessage("You need to specify a hex code, like this: \"#rrggbb\"");
            return true;
        }

        String hex = args[0];
        String cmd = "lp user " + player.getName() + " meta setprefix 100 \"" + hex + "\"";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        return true;
    }
}