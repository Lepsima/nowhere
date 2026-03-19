package tg.lepsima.nowhere;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

import java.util.UUID;

public class Nowhere implements Listener {
    public static final String DIMENSION = "world_nowhere_nowhere";
    public static final String BYPASS_PERMISSION = "nowhere.bypass";

    private final NamespacedKey enterKey;
    private final NamespacedKey exitKey;
    private final Cooldown cooldown = new Cooldown(2000);


    public Nowhere(Plugin plugin) {
        enterKey = new NamespacedKey(plugin, "enter_key");
        exitKey = new NamespacedKey(plugin, "exit_key");
    }

    private static boolean isNowhere(Player player) {
        return player.getWorld().getName().equals(DIMENSION);
    }

    private static boolean isRestricted(Player player) {
        return !player.hasPermission(BYPASS_PERMISSION);
    }

    // region - Dimension Restrictions -
    @EventHandler
    public void onElytraFly(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (isNowhere(player)) {
            if (event.isGliding()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (isNowhere(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        if (isNowhere(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player player) {
            if (isNowhere(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CONSUMABLE_EFFECT) {
            if (isNowhere(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(isRestricted(player) && isNowhere(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(isRestricted(player) && isNowhere(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (isNowhere(player)) {
            event.setCancelled(true);
        }
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
