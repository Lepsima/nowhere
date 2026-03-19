package tg.lepsima.nowhere;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class Nowhere implements Listener {
    public static final String DIMENSION = "world_nowhere_nowhere";
    public static final String BYPASS_PERMISSION = "nowhere.bypass";

    public static Nowhere Instance;

    private final NamespacedKey enterKey;
    private final NamespacedKey exitKey;
    private final Cooldown cooldown = new Cooldown(2000);

    public Nowhere(Plugin plugin) {
        Nowhere.Instance = this;
        enterKey = new NamespacedKey(plugin, "enter_key");
        exitKey = new NamespacedKey(plugin, "exit_key");
    }

    public static boolean isNowhere(Player player) {
        return player.getWorld().getName().equals(DIMENSION);
    }

    public static boolean isRestricted(Player player) {
        return !player.hasPermission(BYPASS_PERMISSION);
    }

    public static ItemStack generateKey(String name, Material material, String model, String code) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Set name
        meta.displayName(Component.text(name)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        // Set lore
        meta.lore(List.of(
                Component.text("", NamedTextColor.DARK_PURPLE),
                Component.text("Right-click to use", NamedTextColor.DARK_PURPLE)
        ));

        // Set model
        meta.setItemModel(new NamespacedKey("minecraft", model));

        // Set custom tag
        NamespacedKey key = new NamespacedKey("nowhere", code);
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);

        return item;
    }

    // region - Dimension Restrictions -
    private void cancelNowhereEvent(Player player, Cancellable event) {
        if (isNowhere(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onElytraFly(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.isGliding()) {
            cancelNowhereEvent(player, event);
        }
    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        cancelNowhereEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        cancelNowhereEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        cancelNowhereEvent(player, event);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.CONSUMABLE_EFFECT) return;
        cancelNowhereEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(isRestricted(player)) {
            cancelNowhereEvent(player, event);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(isRestricted(player)) {
            cancelNowhereEvent(player, event);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        cancelNowhereEvent(player, event);
    }
    // endregion

    // region - Teleportation -
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        // Skip items without meta
        ItemStack item = event.getItem();
        if (!item.hasItemMeta()) return;

        // Get data
        Player player = event.getPlayer();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Check for falsified keys
        boolean isOriginal = true;
        if (meta instanceof BookMeta bookMeta) {
            isOriginal = bookMeta.getGeneration() == BookMeta.Generation.ORIGINAL;
        }

        // Teleport in
        if(data.has(enterKey, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            teleport(player, true, isOriginal);
        }

        // Teleport out
        if(data.has(exitKey, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            teleport(player, false, isOriginal);
        }
    }

    public void teleport(Player player, boolean toNowhere, boolean isOriginal) {
        if (isNowhere(player) == toNowhere) return;

        // Check cooldown
        UUID uuid = player.getUniqueId();
        if (!cooldown.isAvailable(uuid)) return;
        cooldown.startCooldown(uuid);

        // Teleport
        String cmd = toNowhere ? Main.TP_NOWHERE_CMD : Main.TP_WORLD_CMD;
        Main.executeCommandForPlayer(cmd, player);

        // Remove old key
        PlayerInventory inv = player.getInventory();
        inv.setItemInMainHand(inv.getItemInMainHand().subtract(1));

        // Give back return key if needed
        if (toNowhere || !isRestricted(player)) {
            String key_cmd = toNowhere ? Main.EXIT_KEY_CMD : Main.ENTER_KEY_CMD;
            Main.executeCommandForPlayer(key_cmd, player);
        }

        // When using cloned key
        if (!isOriginal) {
            player.sendMessage("I know what you've done . . .");
        }
    }
    // endregion
}
