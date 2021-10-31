package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentCommands {

    private final Array plugin;

    @Command(name = "host", aliases = "start", desc = "Start a tournament")
    @Require("array.tournament.host")
    public void host(@Sender Player player) {

    }

    @Command(name = "join", desc = "Join the on-going tournament")
    public void join(@Sender Player player) {

    }

    @Command(name = "leave", aliases = "quit", desc = "Leave the on-going tournament")
    public void leave(@Sender Player player) {

    }

    public void cancel(@Sender Player player) {

    }

    public void status(@Sender Player player) {

    }

    public void spectate(@Sender Player player) {

    }

    public void announce() {

    }
}
