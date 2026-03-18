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
import tg.lepsima.nowhere.TGCommand;

public class ExitItemCommand extends TGCommand implements CommandExecutor {
    private final Plugin plugin;

    public ExitItemCommand(String command, Plugin plugin) {
        super(command);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Please specify the name of the player to give the key to");
            return true;
        }

        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Exit Key")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.setItemModel(new NamespacedKey("minecraft", "barrier"));

        NamespacedKey key = new NamespacedKey(plugin, "exit_key");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);

        Player player = Bukkit.getPlayer(args[0]);
        if (player != null) {
            player.getInventory().addItem(item);
        }
        return true;
    }
}