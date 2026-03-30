package me.lepsima.nowhere.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
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
import org.bukkit.plugin.java.JavaPlugin;
import me.lepsima.nowhere.Cooldown;
import me.lepsima.nowhere.Main;
import me.lepsima.nowhere.kraber.Kraber;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Nowhere implements Listener {
    public static final String DIMENSION = "world_nowhere_nowhere";
    public static final String BYPASS_PERMISSION = "nowhere.bypass";

    public static Nowhere Instance;
    public static Kraber Kraber;
    public static JavaPlugin Plugin;

    private final NamespacedKey enterKey;
    private final NamespacedKey exitKey;
    private final Cooldown cooldown = new Cooldown(2000);

    public Nowhere(JavaPlugin plugin) {
        Nowhere.Instance = this;
        Nowhere.Kraber = new Kraber();
        Nowhere.Plugin = plugin;

        enterKey = new NamespacedKey(plugin, "enter_key");
        exitKey = new NamespacedKey(plugin, "exit_key");

        IntroEventManager.init(plugin);
    }

    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(Nowhere.Plugin, Nowhere::periodicSearch, 0L, 20L);
    }

    public static void periodicSearch() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        int playerCount = players.size();
        //Kraber.setPlayerCount(playerCount);

        for (Player player : players) {
            Location location = player.getLocation();
            //Kraber.handlePlayer(player);

            if (isInsidePortal(location) && !IntroEventManager.isInList(player.getUniqueId())) {
                player.sendMessage("796F75");
                IntroEventManager.addPlayer(player.getUniqueId());
                Nowhere.Instance.teleport(player, false, true);
            }
        }
    }

    //region - Functions -
    public static boolean isNowhere(Player player) {
        return player.getWorld().getName().equals(DIMENSION);
    }
    public static boolean isNowhere(Location location) {
        return location.getWorld().getName().equals(DIMENSION);
    }

    public static boolean isRestricted(Player player) {
        return !player.hasPermission(BYPASS_PERMISSION);
    }

    public static boolean isInsidePortal(Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double minX = 0;
        double maxX = 1;

        double minY = 37;
        double maxY = 41;

        double minZ = -1;
        double maxZ = 2;

        boolean isInArea =
                x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;

        if (!isInArea) return false;

        return isNowhere(loc);
    }

    public static ItemStack generateKey(String name, Material material, String model, String code) {
        ItemStack item = ItemStack.of(material);
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

    public void removeItemsWithKey(Player player, NamespacedKey key) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(key, PersistentDataType.INTEGER)) {
                contents[i] = null;
            }
        }

        inv.setContents(contents);
    }
    //endregion

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

            if (!isOriginal) {
                player.sendMessage("nue vfa uapv csvmrf nmtr lcl fm gozv xw");
                removeEnterKey(player);
            } else {
                teleport(player, true, true);
            }
        }

        // Teleport out
        if(data.has(exitKey, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            teleport(player, false, true);
        }
    }

    public void removeEnterKey(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.setItemInMainHand(inv.getItemInMainHand().subtract(1));
    }

    public void removeExitKeys(Player player) {
        removeItemsWithKey(player, exitKey);
    }

    public void teleport(Player player, boolean toNowhere, boolean removeKeys) {
        if (isNowhere(player) == toNowhere) return;

        // Check cooldown
        UUID uuid = player.getUniqueId();
        if (!cooldown.isAvailable(uuid)) return;
        cooldown.startCooldown(uuid);

        if (removeKeys) {
            if (toNowhere) {
                removeEnterKey(player);
            } else {
                removeExitKeys(player);
            }
        }

        // Teleport
        String cmd = toNowhere ? Main.TP_NOWHERE_CMD : Main.TP_WORLD_CMD;
        Main.executeCommandForPlayer(cmd, player);

        // Give back return key if needed
        if (toNowhere || !isRestricted(player)) {
            String key_cmd = toNowhere ? Main.EXIT_KEY_CMD : Main.ENTER_KEY_CMD;
            Main.executeCommandForPlayer(key_cmd, player);
        }
    }
    // endregion
}
