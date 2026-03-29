package me.lepsima.nowhere.commands.event;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.lepsima.nowhere.event.IntroEventManager;
import me.lepsima.nowhere.TGCommand;

public class SetIntroEventCommand extends TGCommand implements CommandExecutor {
    public SetIntroEventCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Please specify the name of the player and the state of completion of the event");
            return true;
        }

        boolean isComplete = Boolean.parseBoolean(args[1]);

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return true;

        if (isComplete) {
            IntroEventManager.addPlayer(player.getUniqueId());
        } else {
            IntroEventManager.removePlayer(player.getUniqueId());
        }

        return true;
    }
}