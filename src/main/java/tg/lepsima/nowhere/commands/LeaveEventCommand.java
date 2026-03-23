package tg.lepsima.nowhere.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tg.lepsima.nowhere.Main;
import tg.lepsima.nowhere.Nowhere;
import tg.lepsima.nowhere.TGCommand;

public class LeaveEventCommand extends TGCommand implements CommandExecutor {
    public LeaveEventCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team leave " + player.getName());

        // If leaves the event inside nowhere, teleport out and remove keys
        if (Nowhere.isNowhere(player)){
            Nowhere.Instance.teleport(player, false, true);
        }
        return true;
    }
}