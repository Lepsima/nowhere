package me.lepsima.nowhere;

import me.lepsima.nowhere.commands.event.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import me.lepsima.nowhere.commands.bank.BankAdminCommand;
import me.lepsima.nowhere.commands.bank.BankManageCommand;
import me.lepsima.nowhere.commands.bank.BankTradeCommand;
import me.lepsima.nowhere.commands.event.*;
import me.lepsima.nowhere.commands.util.ChatColorCommand;
import me.lepsima.nowhere.commands.util.GuidebookCommand;
import me.lepsima.nowhere.economy.Bank;
import me.lepsima.nowhere.event.Nowhere;

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
    public static final String INTRO_EVENT_CMD = "introevent";
    public static final String SET_INTRO_EVENT_CMD = "setintroevent";

    public static final String BANK_ADMIN_CMD = "bankadmin";
    public static final String BANK_MANAGE_CMD = "bankmanage";
    public static final String BANK_TRADE_CMD = "banktrade";

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
                new EnterItemCommand(ENTER_KEY_CMD),
                new ExitItemCommand(EXIT_KEY_CMD),
                new IntroEventCommand(INTRO_EVENT_CMD),
                new SetIntroEventCommand(SET_INTRO_EVENT_CMD),

                new BankAdminCommand(BANK_ADMIN_CMD),
                new BankManageCommand(BANK_MANAGE_CMD),
                new BankTradeCommand(BANK_TRADE_CMD),
        };

        for (TGCommand command : commands) {
            String cmd = command.getCommand();
            Objects.requireNonNull(getCommand(cmd)).setExecutor(command);
        }

        nowhere.onEnable();

        ConfigurationSerialization.registerClass(Bank.class);
        saveResource(Bank.RESOURCE_PATH, false);
        Bank.loadBanks();
    }

    @Override
    public void onDisable() {
        Bank.saveBanks();
    }

    public static void executeCommandForPlayer(String cmd, Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + " " + player.getName());
    }
}