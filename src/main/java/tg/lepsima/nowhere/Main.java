package tg.lepsima.nowhere;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import tg.lepsima.nowhere.commands.*;

import java.util.Objects;

public class Main extends JavaPlugin implements Listener {
    public static final String GUIDEBOOK_CMD = "guidebook";
    public static final String CHAT_COLOR_CMD = "chatcolor";
    public static final String JOIN_EVENT_CMD = "joinevent";
    public static final String LEAVE_EVENT_CMD = "leaveevent";
    public static final String ENTER_KEY_CMD = "getenterkey";
    public static final String EXIT_KEY_CMD = "getexitkey";
    public static final String TP_NOWHERE_CMD = "tpnowhere";
    public static final String TP_WORLD_CMD = "tpworld";

    private final Nowhere nowhere = new Nowhere(this);

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(nowhere, this);

        TGCommand[] commands = new TGCommand[] {
                new GuidebookCommand(GUIDEBOOK_CMD),
                new ChatColorCommand(CHAT_COLOR_CMD),
                new JoinEventCommand(JOIN_EVENT_CMD),
                new LeaveEventCommand(LEAVE_EVENT_CMD),
                new TpNowhereCommand(TP_NOWHERE_CMD),
                new TpWorldCommand(TP_WORLD_CMD),
                new EnterItemCommand(ENTER_KEY_CMD,this),
                new ExitItemCommand(EXIT_KEY_CMD, this)
        };

        for (TGCommand command : commands) {
            String cmd = command.getCommand();
            Objects.requireNonNull(getCommand(cmd)).setExecutor(command);
        }
    }

    public static void executeCommandForPlayer(String cmd, Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + " " + player.getName());
    }
}