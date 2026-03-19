package tg.lepsima.nowhere.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import tg.lepsima.nowhere.Nowhere;
import tg.lepsima.nowhere.TGCommand;

import java.util.List;

public class ExitItemCommand extends TGCommand implements CommandExecutor {
    public ExitItemCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Please specify the name of the player to give the key to");
            return true;
        }

        ItemStack item = Nowhere.generateKey("Exit Key", Material.WRITTEN_BOOK, "barrier", "exit_key");
        Player player = Bukkit.getPlayer(args[0]);

        if (player != null) {
            player.getInventory().addItem(item);
        }
        return true;
    }
}