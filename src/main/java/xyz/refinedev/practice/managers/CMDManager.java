package xyz.refinedev.practice.managers;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.cmds.*;
import xyz.refinedev.practice.cmds.event.*;
import xyz.refinedev.practice.cmds.settings.*;
import xyz.refinedev.practice.cmds.standalone.*;
import xyz.refinedev.practice.cmds.*;
import xyz.refinedev.practice.cmds.event.*;
import xyz.refinedev.practice.cmds.standalone.*;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.util.command.CommandService;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

public class CMDManager {

    /**
     * Quick Note to Source Viewers
     *
     * This Command API is NOT made by me, it was simply open source on github
     * by the name "Drink". Its original author is Jonah Seguin.
     *
     * @see https://github.com/jonahseguin/drink
     */

    public final CommandService drink = Array.getDrink();

    public void init() {
        Array.logger("&7Registering Commands...");
        //These are the cmds which have proper sub cmds in them
        drink.register(new ArrayCommands(), "array", "practice");
        drink.register(new ArenaCommands(), "arena", "arenas");
        drink.register(new KitCommands(), "kit", "kits");
        drink.register(new DuelCommands(), "duel");
        drink.register(new RematchCommand(), "rematch");
        drink.register(new PartyCommands(), "party", "p");
        drink.register(new ClanCommands(), "clan", "c");
        drink.register(new EventCommands(), "event", "events");
        drink.register(new SumoCommands(), "sumo");

        //These are standalone cmds which cannot have sub cmds
        drink.register(new ViewInvCommand(), "viewinv", "viewinventory", "inventory");
        drink.register(new SpectateCommand(), "spec", "spectate");
        drink.register(new StopSpecCommand(), "stopspec", "leavespec", "leave spec", "leave spectator", "stop spectating", "stopspectating");
        drink.register(new LeaveMatchCommand(), "forfeit", "abort", "abortmatch", "match abort", "match forfeit", "leave", "suicide");
        drink.register(new AbortMatchCommand(), "cancelmatch", "forfeitmatch", "abortmatch");
        drink.register(new SettingsCommand(), "settings", "preferences", "practicesettings", "pracsettings");
        drink.register(new MapCommand(), "map");
        if (Essentials.getMeta().isRatingEnabled()) drink.register(new RateCommand(), "rate");
        drink.register(new FlyCommand(), "fly", "flight");
        drink.register(new LeaderboardsCommand(), "leaderboards", "lb", "leaderboard");
        drink.register(new StatsCommand(), "stats", "elo", "statistics");

        //Settings Commands
        drink.register(new ToggleScoreboardCMD(), "tsb", "togglescoreboard");
        drink.register(new ToggleDuelCMD(), "tdr", "toggleduels", "toggledr", "toggleduelrequests");
        drink.register(new TogglePingFactorCMD(), "tpf", "togglepf", "togglepingfactor");
        drink.register(new ToggleSpectatorsCMD(), "tsp", "togglesp", "togglespec", "togglespectators");
        drink.register(new ToggleTournamentMessagesCMD(), "ttm", "toggletm", "toggletourneymessages", "toggletournamentmessages");
        drink.register(new TogglePlayersCMD(), "tpv", "toggleplayers", "toggleps", "togglevisibility", "togglehider");
        drink.register(new ToggleDropProtectCMD(), "tdp", "toggledropprotect", "toggledropp", "toggledprotect", "toggledp");
        drink.register(new TogglePingScoreboardCMD(), "tpsb", "togglepingsb", "togglepingscoreboard");
        drink.register(new ToggleCPSScoreboardCMD(), "tcpssb", "togglecpssb", "togglecps", "togglecpsscoreboard");

        drink.registerCommands();
    }
}
