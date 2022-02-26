package xyz.refinedev.practice.managers;

import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaProvider;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.ArenaTypeProvider;
import xyz.refinedev.practice.arena.rating.RatingType;
import xyz.refinedev.practice.arena.rating.RatingTypeProvider;
import xyz.refinedev.practice.clan.ClanProfileProvider;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.cmds.*;
import xyz.refinedev.practice.cmds.essentials.*;
import xyz.refinedev.practice.cmds.settings.*;
import xyz.refinedev.practice.cmds.standalone.*;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitProvider;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileProvider;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.util.command.command.CommandService;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class CommandsManager {

    private final Array plugin;
    public final CommandService drink;

    public void init() {
        this.registerProviders();

        //These are the cmds which have proper sub cmds in them
        drink.register(new ArrayCommands(plugin), "array", "practice");
        drink.register(new ArenaCommands(plugin), "arena", "arenas");
        drink.register(new KitCommands(plugin), "kit", "kits");
        drink.register(new DuelCommands(plugin, plugin.getCoreHandler()), "duel");
        drink.register(new RematchCommand(plugin), "rematch");
        drink.register(new PartyCommands(plugin), "party", "p");
        drink.register(new TournamentCommands(plugin), "tournament", "tourney");
        drink.register(new ClanCommands(plugin), "clan", "c");
        /*drink.register(new EventCommands(plugin, plugin.getEventManager()), "event", "events");
        drink.register(new SumoCommands(plugin), "sumo");
        drink.register(new SpleefCommands(plugin, plugin.getEventManager()), "spleef");
        drink.register(new ParkourCommands(plugin, plugin.getEventManager()), "parkour");*/

        //These are standalone cmds which cannot have sub cmds
        drink.register(new ViewInvCommand(plugin), "viewinv", "viewinventory", "inventory");
        drink.register(new SpectateCommand(plugin), "spec", "spectate");
        drink.register(new StopSpecCommand(plugin), "stopspec", "leavespec", "leave spec", "leave spectator", "stop spectating", "stopspectating");
        drink.register(new LeaveMatchCommand(plugin), "forfeit", "abort", "abortmatch", "match abort", "match forfeit", "leave", "suicide");
        drink.register(new AbortMatchCommand(plugin), "cancelmatch", "forfeitmatch", "abortmatch");
        drink.register(new SettingsCommand(plugin), "settings", "preferences", "practicesettings", "pracsettings");
        drink.register(new MapCommand(plugin), "map");
        drink.register(new FlyCommand(plugin), "fly", "flight");
        drink.register(new LeaderboardsCommand(plugin), "leaderboards", "lb", "leaderboard");
        drink.register(new OpenMenuCMD(plugin), "openmenu", "menu", "menus");
        drink.register(new StatsCommand(plugin), "stats", "elo", "statistics");
        drink.register(new SpectateMenuCommand(plugin), "specmenu", "spectatemenu");
        drink.register(new TestCommand(), "test");

        //Essentials Commands
        drink.register(new UnrankedQueueCMD(plugin), "unrankedqueue", "queue", "queue unranked");
        drink.register(new RankedQueueCMD(plugin), "rankedqueue", "queue ranked");
        drink.register(new ClanQueueCMD(plugin), "clanqueue", "queue clan");
        drink.register(new LeaveQueueCMD(plugin), "leavequeue", "queue leave");
        drink.register(new KitEditorCMD(plugin) ,"kiteditor", "editkit");
        drink.register(new BuildCMD(plugin), "build", "buildmode");
        drink.register(new SilentCMD(plugin), "silent", "silentmode");

        //Settings Commands
        drink.register(new ToggleScoreboardCMD(plugin), "tsb", "togglescoreboard");
        drink.register(new ToggleDuelCMD(plugin), "tdr", "toggleduels", "toggledr", "toggleduelrequests");
        drink.register(new TogglePingFactorCMD(plugin), "tpf", "togglepf", "togglepingfactor");
        drink.register(new ToggleSpectatorsCMD(plugin), "tsp", "togglesp", "togglespec", "togglespectators");
        drink.register(new ToggleTournamentMessagesCMD(plugin), "ttm", "toggletm", "toggletourneymessages", "toggletournamentmessages");
        drink.register(new TogglePlayersCMD(plugin), "tpv", "toggleplayers", "toggleps", "togglevisibility", "togglehider");
        drink.register(new ToggleDropProtectCMD(plugin), "tdp", "toggledropprotect", "toggledropp", "toggledprotect", "toggledp");

        if (plugin.getConfigHandler().isPING_SCOREBOARD_SETTING())
            drink.register(new TogglePingScoreboardCMD(plugin), "tpsb", "togglepingsb", "togglepingscoreboard");
        if (plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING())
            drink.register(new ToggleCPSScoreboardCMD(plugin), "tcpssb", "togglecpssb", "togglecps", "togglecpsscoreboard");
        if (plugin.getConfigHandler().isRATINGS_ENABLED())
            drink.register(new RateCommand(plugin), "rate");

        drink.registerCommands();
    }

    public void registerProviders() {
        drink.bind(Arena.class).toProvider(new ArenaProvider());
        drink.bind(ClanProfile.class).toProvider(new ClanProfileProvider());
        drink.bind(ArenaType.class).toProvider(new ArenaTypeProvider());
        drink.bind(Kit.class).toProvider(new KitProvider());
        drink.bind(Profile.class).toProvider(new ProfileProvider());
        //drink.bind(EventType.class).toProvider(new EventProvider());
        drink.bind(RatingType.class).toProvider(new RatingTypeProvider());
    }
}
