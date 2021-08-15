package me.drizzy.practice.managers;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.command.*;
import me.drizzy.practice.essentials.commands.ArrayCommand;
import me.drizzy.practice.essentials.commands.*;
import me.drizzy.practice.essentials.commands.donator.FlyCommand;
import me.drizzy.practice.duel.command.DuelAcceptCommand;
import me.drizzy.practice.duel.command.DuelCommand;
import me.drizzy.practice.duel.command.RematchCommand;
import me.drizzy.practice.events.EventCommand;
import me.drizzy.practice.events.EventHelpCommand;
import me.drizzy.practice.events.types.brackets.command.*;
import me.drizzy.practice.events.types.gulag.command.*;
import me.drizzy.practice.events.types.lms.command.*;
import me.drizzy.practice.events.types.parkour.command.*;
import me.drizzy.practice.events.types.spleef.command.*;
import me.drizzy.practice.events.types.sumo.command.*;
import me.drizzy.practice.kit.command.*;
import me.drizzy.practice.match.command.*;
import me.drizzy.practice.party.command.*;
import me.drizzy.practice.leaderboards.command.LeaderboardsCommand;
import me.drizzy.practice.settings.commands.ToggleDuelsCommand;
import me.drizzy.practice.settings.commands.ToggleScoreboardCommand;
import me.drizzy.practice.statistics.command.StatsCommand;
import me.drizzy.practice.tournament.command.*;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class CommandsManager {
    
    public static void register() {
        for (Object command : Arrays.asList(
                //Staff commands
                new CancelMatchCommand(),

                //Array Commands
                new ArrayCommand(),
                new ArraySetLobbyCommand(),
                new ArrayGoldenHeadCommand(),
                new ArenaSetDuplicateCommand(),
                new ArrayReloadDivisionsCommand(),
                new ArrayRenameCommand(),
                new ArrayRefillCommand(),
                new ArrayResetStatsCommand(),
                new ArraySaveArenasCommand(),
                new ArraySaveCommand(),
                new ArraySaveDataCommand(),
                new ArraySaveKitsCommand(),
                new ArrayPracticeCommand(),
                new ArrayHCFCommand(),
                new ArrayClearLoadoutsCommand(),
                new ArraySpawnCommand(),
                new ToggleScoreboardCommand(),
                new ToggleDuelsCommand(),
                new ArrayVerCommand(),
                new ArrayWorldCommand(),

                //Player command
                new LeaderboardsCommand(),
                new ArraySettingsCommand(),
                new EventCommand(),
                new EventHelpCommand(),
                new FlyCommand(),

                //Arena commands
                new ArenaAddKitCommand(),
                new ArenaRemoveKitCommand(),
                new ArenaAddBuildKitsCommand(),
                new ArenaSetPortalCommand(),
                new ArenaDisablePearlsCommand(),
                new ArenaSetCuboidCommand(),
                new ArenaSelectionWandCommand(),
                new ArenaSetSpawnCommand(),
                new ArenaSetMaxCommand(),
                new ArenaSetMinCommand(),
                new ArenaCreateCommand(),
                new ArenaAddNormalKitCommand(),
                new ArenaRemoveCommand(),
                new ArenasCommand(),
                new ArenaTpCommand(),
                new ArenaCommand(),
                new ArenaSetBridgdeSpawnCommand(),
                new ArenasCommand(),
                new ArenaKitListCommand(),
                new ArenaSaveCommand(),
                new ArenaReloadCommand(),
                new ArenaSetIconCommand(),
                new ArenaSetDisplayNameCommand(),

                //Duel commands
                new DuelCommand(),
                new DuelAcceptCommand(),
                new RematchCommand(),
                new ViewInventoryCommand(),
                new SpectateCommand(),
                new StopSpectatingCommand(),
                new MatchStatusCommand(),

                //Party command
                new PartyCloseCommand(),
                new PartyCreateCommand(),
                new PartyDisbandCommand(),
                new PartyHelpCommand(),
                new PartyInfoCommand(),
                new PartyInviteCommand(),
                new PartyJoinCommand(),
                new PartyKickCommand(),
                new PartyLeaveCommand(),
                new PartyChatCommand(),
                new PartyOpenCommand(),
                new PartyLeaderCommand(),
                new PartyUnbanCommand(),
                new PartyBanCommand(),

                //Kit command
                new KitCreateCommand(),
                new KitGetInvCommand(),
                new KitSetInvCommand(),
                new KitSetHitDelayCommand(),
                new KitSetKnockbackProfileCommand(),
                new KitListCommand(),
                new KitCommand(),
                new KitSaveCommand(),
                new KitRemoveCommand(),
                new KitSetIconCommand(),
                new KitBridgeCommand(),
                new KitSetRankedCommand(),
                new KitSumoCommand(),
                new KitBuildCommand(),
                new KitBoxUHCCommand(),
                new KitSetAntiFoodLossCommand(),
                new KitSetBowHPCommand(),
                new KitSetHealthRegenenerationCommand(),
                new KitSetInfiniteSpeedCommand(),
                new KitSetInfiniteStrengthCommand(),
                new KitComboCommand(),
                new KitSpleefCommand(),
                new KitTimedCommand(),
                new KitParkourCommand(),
                new KitPartyFFACommand(),
                new KitPartySplitCommand(),
                new KitDisableCommand(),
                new KitLavaKillCommand(),
                new KitSetFallDamageCommand(),
                new KitWaterKillCommand(),
                new KitSetNoItemsCommand(),
                new KitShowHealthCommand(),
                new KitDisableCommand(),
                new KitEnableCommand(),
                new KitVoidSpawnCommand(),
                new KitStickSpawnCommand(),
                new KitSetDisplayNameCommand(),

                //Brackets commands
                new BracketsLeaveCommand(),
                new BracketsCancelCommand(),
                new BracketsCooldownCommand(),
                new BracketsJoinCommand(),
                new BracketsSetSpawnCommand(),
                new BracketsHostCommand(),
                new BracketsTpCommand(),
                new BracketsHelpCommand(),
                new BracketsKnockbackCommand(),
                new BracketsForceStartCommand(),

                //Gulag Commands
                new GulagLeaveCommand(),
                new GulagCancelCommand(),
                new GulagCooldownCommand(),
                new GulagJoinCommand(),
                new GulagForceStartCommand(),
                new GulagHostCommand(),
                new GulagHelpCommand(),
                new GulagSetSpawnCommand(),
                new GulagTpCommand(),
                new GulagKnockbackCommand(),

                //Sumo commands
                new SumoCancelCommand(),
                new SumoCooldownCommand(),
                new SumoHostCommand(),
                new SumoJoinCommand(),
                new SumoLeaveCommand(),
                new SumoSetSpawnCommand(),
                new SumoTpCommand(),
                new SumoHelpCommand(),
                new SumoKnockbackCommand(),
                new SumoForceStartCommand(),

                //LMS commands
                new LMSCancelCommand(),
                new LMSCooldownCommand(),
                new LMSKnockbackCommand(),
                new LMSHostCommand(),
                new LMSJoinCommand(),
                new LMSLeaveCommand(),
                new LMSSetSpawnCommand(),
                new LMSTpCommand(),
                new LMSHelpCommand(),
                new LMSForceStartCommand(),

                //Parkour commands
                new ParkourCancelCommand(),
                new ParkourCooldownCommand(),
                new ParkourHostCommand(),
                new ParkourJoinCommand(),
                new ParkourLeaveCommand(),
                new ParkourSetSpawnCommand(),
                new ParkourTpCommand(),
                new ParkourHelpCommand(),
                new ParkourForceStartCommand(),

                //Spleef commands
                new SpleefCancelCommand(),
                new SpleefCooldownCommand(),
                new SpleefHostCommand(),
                new SpleefJoinCommand(),
                new SpleefLeaveCommand(),
                new SpleefKnockbackCommand(),
                new SpleefSetSpawnCommand(),
                new SpleefTpCommand(),
                new SpleefHelpCommand(),
                new SpleefForceStartCommand(),

                //Tournament commands
                new TournamentCommand(),
                new TournamentLeaveCommand(),
                new TournamentJoinCommand(),
                new TournamentHostCommand(),
                new TournamentCancelCommand(),
                new TournamentListCommand()
        ))

        Array.getHoncho().registerCommand(command);

        if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit") && Bukkit.getPluginManager().isPluginEnabled("WorldEdit") ) {
            Array.getHoncho().registerCommand(new ArenaGenerateCommand());
        } else {
            Array.logger("&cWorld Edit or FAWE not found, Arena Generating will not work!");
        }

        Bukkit.getCommandMap().register("stats", new StatsCommand());
    }
}
