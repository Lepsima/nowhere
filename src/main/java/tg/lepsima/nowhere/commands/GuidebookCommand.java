package tg.lepsima.nowhere.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tg.lepsima.nowhere.Cooldown;
import tg.lepsima.nowhere.TGCommand;

import java.util.UUID;

public class GuidebookCommand extends TGCommand implements CommandExecutor {
    private static final String GUIDEBOOK_COMMAND = "written_book[written_book_content={pages:[[[\" \",{\"text\":\"VexaSMP Season 4\",\"bold\":true},\"\\nCommon sense, don't grief, cheat and whatever\\n\\nThere's anti-grief and some moderation\\n\\nIf many people are online, don't stress the server too much with redstone or entities\"]],[[\" \",{\"text\":\"Guide Book\",\"bold\":true},\"\\n\\nYou can obtain this book using\\n\",{\"text\":\"/guidebook\",\"color\":\"gold\"},\"\\n\\n\",{\"text\":\"Plugins available:\",\"bold\":true},\"\\n- password login \\n- fast login\\n- skin changer\\n- tpaccept\"]],[[\" \",{\"text\":\"Login / Fast Login\",\"bold\":true},\"\\n\\nYour session should remain open for +12H after logging in\\n\\n\",{\"text\":\"Premium accounts \",\"color\":\"red\"},\"can log in without password (like normal servers)\\n\\n\",{\"text\":\"Password change\",\"bold\":true},\"\\n\",{\"text\":\"/changepassword <old> <new>\",\"italic\":true,\"color\":\"gold\"}]],[[\" \",{\"text\":\"Skin Restorer\",\"bold\":true},\"\\n\\nCracked accoutns can mess a bit the skin system, you can do the following:\\n\\n\",{\"text\":\"Skin Change\",\"bold\":true},\"\\n\",{\"text\":\"/skin set <username>\",\"italic\":true,\"color\":\"gold\"},\"\\n\\nthis will change your skin, specially needed for non-premium\"]],[[\" \",{\"text\":\"TPA\",\"bold\":true},\"\\ntpa is enabled with a 4 minute cooldown\\n\",{\"text\":\"/tpa <player>\",\"color\":\"gold\"},\"\\n\\nYou can accept /decline in the GUI\\n\\nFor other uses type \",{\"text\":\"/tpa...\",\"color\":\"gold\"},\"\\nand investigate the suggested commands\"]]],title:\"Server book\",author:Lepsima,generation:0},rarity=epic,item_model=\"minecraft:knowledge_book\"]";
    private static final int COOLDOWN_MILLIS = 60_000;
    private final Cooldown cooldown = new Cooldown(COOLDOWN_MILLIS);

    public GuidebookCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        UUID uuid = player.getUniqueId();

        if (cooldown.isAvailable(uuid)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getName() + " " + GUIDEBOOK_COMMAND);
            cooldown.startCooldown(uuid);
        } else {
            long timeLeft = cooldown.getTimeLeft(uuid);
            player.sendMessage("§cPlease wait " + timeLeft + " before asking for another guidebook.");
        }

        return true;
    }
}