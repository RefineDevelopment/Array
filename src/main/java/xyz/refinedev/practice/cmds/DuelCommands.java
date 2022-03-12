package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.impl.StandaloneArena;
import xyz.refinedev.practice.duel.DuelProcedure;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.duel.menu.DuelSelectKitMenu;
import xyz.refinedev.practice.hook.core.CoreHandler;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.MatchManager;
import xyz.refinedev.practice.managers.PartyManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.other.PlayerUtil;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/25/2021
 * Project: Array
 */

//TODO: Recode this and party menus
@RequiredArgsConstructor
public class DuelCommands {

    private final Array plugin;

    private Match match;
    private Arena arena;
    private Kit kit;

    @Command(name = "", desc = "Duel a player", usage = "<player>")
    public void duel(@Sender Player player, Player target) {
        ProfileManager profileManager = plugin.getProfileManager();

        Profile senderProfile = profileManager.getProfile(player.getUniqueId());
        Profile receiverProfile = profileManager.getProfile(target.getUniqueId());

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(CC.RED + "You cannot duel yourself.");
            return;
        }
        if (senderProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        if (receiverProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }
        if (!receiverProfile.getSettings().isReceiveDuelRequests()) {
            player.sendMessage(CC.RED + "That player is not accepting any duel requests at the moment.");
            return;
        }
        if (profileManager.isPendingDuelRequest(senderProfile, player)) {
            player.sendMessage(CC.RED + "You have already sent that player a duel request.");
            return;
        }
        if (senderProfile.hasParty() && !receiverProfile.hasParty()) {
            player.sendMessage(Locale.ERROR_TARGET_NO_PARTY.toString());
            return;
        }

        DuelProcedure procedure = new DuelProcedure(plugin, player, target, senderProfile.hasParty());
        senderProfile.setDuelProcedure(procedure);

        Menu menu = new DuelSelectKitMenu();
        plugin.getMenuHandler().openMenu(menu, player);
    }

    @Command(name = "accept", usage = "<player> <target>", desc = "Accept a duel pending request")
    public void duelAccept(@Sender Player player, Player target) {
        ProfileManager profileManager = plugin.getProfileManager();
        PartyManager partyManager = plugin.getPartyManager();
        MatchManager matchManager = plugin.getMatchManager();
        CoreHandler coreHandler = plugin.getCoreHandler();

        Profile senderProfile = profileManager.getProfile(player.getUniqueId());
        Profile receiverProfile = profileManager.getProfile(target.getUniqueId());

        if (senderProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        if (!profileManager.isPendingDuelRequest(receiverProfile, player)) {
            player.sendMessage(Locale.DUEL_NOT_PENDING.toString());
            return;
        }

        if (receiverProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }

        DuelRequest request = receiverProfile.getDuelRequests().get(player.getUniqueId());

        if (request == null) {
            player.sendMessage(Locale.DUEL_NOT_PENDING.toString());
            return;
        }

        if (request.isParty()) {
            if (!senderProfile.hasParty()) {
                player.sendMessage(Locale.ERROR_NO_PARTY_TO_DUEL.toString());
                return;
            } else if (!receiverProfile.hasParty()) {
                player.sendMessage(Locale.ERROR_TARGET_NO_PARTY.toString());
                return;
            }
        } else {
            if (senderProfile.hasParty()) {
                player.sendMessage(Locale.ERROR_PARTY.toString());
                return;
            } else if (receiverProfile.hasParty()) {
                player.sendMessage(Locale.ERROR_TARGET_IN_PARTY.toString());
                return;
            }
        }

        Arena arena = request.getArena();

        if (arena == null) {
            player.sendMessage(Locale.ERROR_NO_ARENAS.toString());
            return;
        }

        if (arena.isActive()) {
            if (arena.getType().equals(ArenaType.STANDALONE)) {
                StandaloneArena sarena = (StandaloneArena) arena;
                arena = this.plugin.getArenaManager().findDuplicate(sarena);

                if (arena == null) {
                    player.sendMessage(CC.RED + "The arena you were dueled was a build arena and all arenas are busy.");
                    return;
                }
            }
        }

        arena.setActive(true);

        this.kit = request.getKit();
        this.arena = arena;

        if (request.isParty()) {
            Party senderParty = partyManager.getPartyByUUID(senderProfile.getUniqueId());
            Party receiverParty = partyManager.getPartyByUUID(receiverProfile.getUniqueId());

            Team teamA = new Team(new TeamPlayer(player));
            for ( Player partyMember : senderParty.getPlayers() ) {
                if (!partyMember.getPlayer().equals(player)) {
                    teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                }
            }

            Team teamB = new Team(new TeamPlayer(target));
            for ( Player partyMember : receiverParty.getPlayers() ) {
                if (!partyMember.getPlayer().equals(target)) {
                    teamB.getTeamPlayers().add(new TeamPlayer(partyMember));
                }
            }
            this.match = matchManager.createTeamKitMatch(teamA, teamB, kit, arena);
        } else {
            TeamPlayer firstMatchPlayer = new TeamPlayer(player);
            TeamPlayer secondMatchPlayer = new TeamPlayer(target);

            this.match = matchManager.createSoloKitMatch(null, firstMatchPlayer, secondMatchPlayer, kit, arena, QueueType.UNRANKED);

            for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
                String opponentMessages = this.formatMessages(string, player, target, QueueType.UNRANKED);
                player.sendMessage(replaceOpponent(opponentMessages, player));
                target.sendMessage(replaceOpponent(opponentMessages, target));
            }
        }
        this.plugin.getServer().getScheduler().runTask(plugin, () -> matchManager.start(match));
    }

    private String formatMessages(String string, Player sender, Player target, QueueType type) {
        ProfileManager profileManager = this.plugin.getProfileManager();

        Profile senderProfile = profileManager.getProfile(sender.getUniqueId());
        Profile targetProfile = profileManager.getProfile(target.getUniqueId());

        int senderELO = senderProfile.getStatisticsData().get(kit).getElo();
        int targetELO = targetProfile.getStatisticsData().get(kit).getElo();

        String senderName = profileManager.getColouredName(senderProfile);
        String targetName = profileManager.getColouredName(targetProfile);

        return string
                .replace("<ranked>", type == QueueType.RANKED ? "&aTrue" : "&cFalse")
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(target)))
                .replace("<player1>", type == QueueType.RANKED ? senderName + CC.GRAY + " (" + senderELO + ")" : senderName)
                .replace("<player2>", type == QueueType.RANKED ? targetName + CC.GRAY + " (" + targetELO + ")" : targetName);
    }

    private String replaceOpponent(String opponent, Player player) {
        opponent = opponent
                .replace("<opponent>", this.match.getOpponentPlayer(player).getDisplayName())
                .replace("<opponent_ping>", String.valueOf(PlayerUtil.getPing(this.match.getOpponentPlayer(player))))
                .replace("<player_ping>", String.valueOf(PlayerUtil.getPing(player)))
                .replace("<arena>", this.arena.getDisplayName())
                .replace("<kit>", this.kit.getDisplayName())
                .replace("<player>", player.getDisplayName());
        return opponent;
    }
}