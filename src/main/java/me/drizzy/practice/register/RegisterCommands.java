package me.drizzy.practice.register;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.command.*;
import me.drizzy.practice.array.ArrayCommand;
import me.drizzy.practice.array.commands.*;
import me.drizzy.practice.array.commands.donator.FlyCommand;
import me.drizzy.practice.array.commands.staff.FollowCommand;
import me.drizzy.practice.array.commands.staff.GetUUIDCommand;
import me.drizzy.practice.array.commands.staff.SilentCommand;
import me.drizzy.practice.array.commands.staff.UnFollowCommand;
import me.drizzy.practice.duel.command.DuelAcceptCommand;
import me.drizzy.practice.duel.command.DuelCommand;
import me.drizzy.practice.duel.command.RematchCommand;
import me.drizzy.practice.event.EventCommand;
import me.drizzy.practice.event.EventHelpCommand;
import me.drizzy.practice.event.types.brackets.command.*;
import me.drizzy.practice.event.types.wizard.command.*;
import me.drizzy.practice.event.types.lms.command.*;
import me.drizzy.practice.event.types.parkour.command.*;
import me.drizzy.practice.event.types.spleef.command.*;
import me.drizzy.practice.event.types.sumo.command.*;
import me.drizzy.practice.kit.command.*;
import me.drizzy.practice.match.command.MatchStatusCommand;
import me.drizzy.practice.match.command.SpectateCommand;
import me.drizzy.practice.match.command.StopSpectatingCommand;
import me.drizzy.practice.match.command.ViewInventoryCommand;
import me.drizzy.practice.party.command.*;
import me.drizzy.practice.statistics.command.LeaderboardsCommand;
import me.drizzy.practice.statistics.command.StatsCommand;
import me.drizzy.practice.tournament.command.*;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class RegisterCommands {
    
    public static void register() {
        Array.logger("&bRegistering Commands...");
        for (Object command : Arrays.asList(
                //Staff commands
                new SilentCommand(),
                new FollowCommand(),
                new UnFollowCommand(),

                //Array Commands
                new ArrayCommand(),
                new ArraySetLobbyCommand(),
                new ArraySetEloCommand(),
                new ArrayGoldenHeadCommand(),
                new ArrayReloadCommand(),
                new ArrayRenameCommand(),
                new ArrayRefillCommand(),
                new ArrayResetStatsCommand(),
                new ArraySaveArenasCommand(),
                new ArraySaveCommand(),
                new ArraySaveDataCommand(),
                new ArraySaveKitsCommand(),
                new ArrayPracticeCommand(),
                new ArrayHCFCommand(),
                new ArraySpawnCommand(),
                new ArrayToggleScoreboardCommand(),
                new ArrayToggleDuelCommand(),
                new ArrayVerCommand(),
                new ArrayWoldCommand(),
                new TestCommand(),

                //Player command
                new LeaderboardsCommand(),
                new ArraySettingsCommand(),
                new EventCommand(),
                new GetUUIDCommand(),
                new EventHelpCommand(),
                new FlyCommand(),

                //Arena commands
                new ArenaAddKitCommand(),
                new ArenaRemoveKitCommand(),
                new ArenaAddBuildKitsCommand(),
                new ArenaDisablePearlsCommand(),
                new ArenaSetPortalCommand(),
                new ArenaPortalWandCommand(),
                new ArenaSetSpawnCommand(),
                new ArenaCreateCommand(),
                new ArenaAddNormalKitCommand(),
                new ArenaRemoveCommand(),
                new ArenasCommand(),
                new ArenaTpCommand(),
                new ArenaCommand(),
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
                new KitSetKnockbackProfileCommand(),
                new KitListCommand(),
                new KitCommand(),
                new KitSaveCommand(),
                new KitRemoveCommand(),
                new KitSetIconCommand(),
                new KitSetHitDelayCommand(),
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

                //Wizard Commands
                new WizardLeaveCommand(),
                new WizardCancelCommand(),
                new WizardCooldownCommand(),
                new WizardJoinCommand(),
                new WizardForceStartCommand(),
                new WizardHostCommand(),
                new WizardHelpCommand(),
                new WizardSetSpawnCommand(),
                new WizardTpCommand(),
                new WizardKnockbackCommand(),

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
                new ParkourForceStartComman(),

                //Spleef commands
                new SpleefCancelCommand(),
                new SpleefCooldownCommand(),
                new SpleefHostCommand(),
                new SpleefJoinCommand(),
                new SpleefLeaveCommand(),
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
        Bukkit.getCommandMap().register("stats", new StatsCommand());
    }
}
