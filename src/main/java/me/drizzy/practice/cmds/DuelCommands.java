package me.drizzy.practice.cmds;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.duel.DuelProcedure;
import me.drizzy.practice.duel.DuelRequest;
import me.drizzy.practice.duel.menu.DuelSelectKitMenu;
import me.drizzy.practice.arena.ArenaType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.rank.Rank;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.HCFMatch;
import me.drizzy.practice.match.types.SoloMatch;
import me.drizzy.practice.match.types.TeamMatch;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Sender;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/25/2021
 * Project: Array
 */

public class DuelCommands {

    private Match match;
    private Arena arena;
    private Kit kit;

    @Command(name = "", desc = "Duel a player", usage = "<player>")
    public void duel(@Sender Player player, Player target) {
        Profile senderProfile = Profile.getByUuid(player.getUniqueId());
        Profile receiverProfile = Profile.getByUuid(target.getUniqueId());

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(CC.RED + "You cannot duel yourself.");
            return;
        }

        if (senderProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_UNAVAILABLE.toString());
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
        if (!senderProfile.canSendDuelRequest(player)) {
            player.sendMessage(CC.RED + "You have already sent that player a duel request.");
            return;
        }
        if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
            player.sendMessage(CC.RED + "That player is not in a party.");
            return;
        }

        DuelProcedure procedure = new DuelProcedure(player, target, senderProfile.getParty() != null);
        senderProfile.setDuelProcedure(procedure);
        new DuelSelectKitMenu().openMenu(player);
    }

    @Command(name = "accept", usage = "<player> <target>", desc = "Accept a duel pending request")
    public void duelAccept(@Sender Player player, Player target) {
        Profile senderProfile = Profile.getByUuid(player.getUniqueId());
        Profile receiverProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_UNAVAILABLE.toString());
            return;
        }

        if (!receiverProfile.isPendingDuelRequest(player)) {
            player.sendMessage(CC.RED + "You do not have a pending duel request from " + target.getName() + CC.RED + ".");
            return;
        }

        if (receiverProfile.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }

        DuelRequest request = receiverProfile.getSentDuelRequests().get(player.getUniqueId());

        if (request == null) {
            return;
        }

        if (request.isParty()) {
            if (senderProfile.getParty() == null) {
                player.sendMessage(CC.RED + "You do not have a party to duel with.");
                return;
            } else if (receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player does not have a party to duel with.");
                return;
            }
        } else {
            if (senderProfile.getParty() != null) {
                player.sendMessage(CC.RED + "You cannot duel whilst in a party.");
                return;
            } else if (receiverProfile.getParty() != null) {
                player.sendMessage(CC.RED + "That player is in a party and cannot duel right now.");
                return;
            }
        }

        Arena arena=request.getArena();

        if (arena == null) {
            player.sendMessage(CC.RED + "Tried to start a match but the arena was invalid.");
            return;
        }

        if (arena.isActive()) {
            if (arena.getType().equals(ArenaType.STANDALONE)) {
                StandaloneArena sarena = (StandaloneArena) arena;
                if (sarena.getDuplicates() != null) {
                    boolean foundarena = false;
                    for ( Arena darena : sarena.getDuplicates() ) {
                        if (!darena.isActive()) {
                            arena = darena;
                            foundarena = true;
                            break;
                        }
                    }
                    if (!foundarena) {
                        player.sendMessage(CC.RED + "The arena you were dueled was a build arena and there were no arenas found.");
                        return;
                    }
                }
            } else if (arena.getType().equals(ArenaType.THEBRIDGE)) {
                player.sendMessage(CC.RED + "The arena you were dueled was a bridge arena and there were no arenas found.");
                return;
            }
        }

        if (!arena.getType().equals(ArenaType.SHARED)) {
            arena.setActive(true);
        }

        kit = request.getKit();
        this.arena = arena;

        if (request.isParty()) {
            Team teamA=new Team(new TeamPlayer(player));
            if (request.getKit().getName().equals("HCFTeamFight")) {

                for ( Player partyMember : senderProfile.getParty().getPlayers() ) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                Team teamB = new Team(new TeamPlayer(target));

                for ( Player partyMember : receiverProfile.getParty().getPlayers() ) {
                    if (!partyMember.getPlayer().equals(target)) {
                        teamB.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                match=new HCFMatch(teamA, teamB, arena);
            } else {
                for ( Player partyMember : senderProfile.getParty().getPlayers() ) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                Team teamB = new Team(new TeamPlayer(target));

                for ( Player partyMember : receiverProfile.getParty().getPlayers() ) {
                    if (!partyMember.getPlayer().equals(target)) {
                        teamB.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }
                match=new TeamMatch(teamA, teamB, request.getKit(), arena);
            }

        } else if (request.getKit().getGameRules().isBridge()) {
            match = new TheBridgeMatch(null, new TeamPlayer(player), new TeamPlayer(target), request.getKit(), arena,
                    QueueType.UNRANKED);
        } else {
            match = new SoloMatch(null, new TeamPlayer(player), new TeamPlayer(target), request.getKit(), arena,
                    QueueType.UNRANKED);
        }
        if (!request.isParty()) {
            for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
                String opponentMessages = this.formatMessages(target, player, string, Rank.getRankType().getFullName(player), Rank.getRankType().getFullName(target), senderProfile.getStatisticsData().get(request.getKit()).getElo(), receiverProfile.getStatisticsData().get(request.getKit()).getElo(), QueueType.UNRANKED);
                player.sendMessage(replaceOpponent(opponentMessages, player));
                target.sendMessage(replaceOpponent(opponentMessages, target));
            }
        }
        match.start();
    }

    private String formatMessages(Player player1P, Player player2P, String string, String player1, String player2, int player1Elo, int player2Elo, QueueType type) {
        return string
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(player1P)))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(player2P)))
                .replace("<player1>", type == QueueType.RANKED ? player1 + CC.GRAY + " (" + player1Elo + ")" : player1)
                .replace("<player2>", type == QueueType.RANKED ? player2 + CC.GRAY + " (" + player2Elo + ")" : player2);
    }

    private String replaceOpponent(String opponent, Player player) {
        opponent = opponent
                .replace("<opponent>", match.getOpponentPlayer(player).getDisplayName())
                .replace("<opponent_ping>", String.valueOf(PlayerUtil.getPing(match.getOpponentPlayer(player))))
                .replace("<player_ping>", String.valueOf(PlayerUtil.getPing(player)))
                .replace("<arena>", this.arena.getDisplayName())
                .replace("<kit>", this.kit.getDisplayName())
                .replace("<player>", player.getDisplayName());
        return opponent;
    }
}