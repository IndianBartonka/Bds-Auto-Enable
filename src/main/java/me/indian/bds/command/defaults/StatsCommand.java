package me.indian.bds.command.defaults;

import me.indian.bds.command.Command;
import me.indian.bds.util.StatusUtil;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", "Aktualne statystyki servera minecraft i maszyny");
    }

    @Override
    public boolean onExecute(final String[] args, final boolean isOp) {
        for (final String stats : StatusUtil.getMainStats(false)) {
            this.sendMessage(stats);
        }
        return true;
    }
}