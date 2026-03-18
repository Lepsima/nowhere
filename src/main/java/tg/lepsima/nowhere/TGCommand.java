package tg.lepsima.nowhere;

import org.bukkit.command.CommandExecutor;

public abstract class TGCommand implements CommandExecutor {
    private final String command;

    public TGCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
