package tg.lepsima.nowhere.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tg.lepsima.nowhere.TGCommand;

public class JoinEventCommand extends TGCommand implements CommandExecutor {
    public JoinEventCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join unknown " + player.getName());
        return true;
    }
}