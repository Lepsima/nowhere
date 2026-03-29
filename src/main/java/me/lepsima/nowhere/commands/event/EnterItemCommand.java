package me.lepsima.nowhere.commands.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.lepsima.nowhere.event.Nowhere;
import me.lepsima.nowhere.TGCommand;

public class EnterItemCommand extends TGCommand implements CommandExecutor {
    public EnterItemCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Please specify the name of the player to give the key to");
            return true;
        }

        ItemStack item = Nowhere.generateKey("Enter Key", Material.WRITTEN_BOOK, "tripwire_hook", "enter_key");
        Player player = Bukkit.getPlayer(args[0]);

        if (player != null) {
            player.getInventory().addItem(item);
        }
        return true;
    }
}