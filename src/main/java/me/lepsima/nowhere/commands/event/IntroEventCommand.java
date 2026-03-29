package me.lepsima.nowhere.commands.event;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.lepsima.nowhere.event.IntroEventManager;
import me.lepsima.nowhere.event.Nowhere;
import me.lepsima.nowhere.TGCommand;

public class IntroEventCommand extends TGCommand implements CommandExecutor {
    public IntroEventCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        if (Nowhere.isNowhere(player) || IntroEventManager.isInList(player.getUniqueId())) {
            return true;
        }

        // Teleport in without key, and give an extra exit key
        Nowhere.Instance.teleport(player, true, false);
        return true;
    }
}