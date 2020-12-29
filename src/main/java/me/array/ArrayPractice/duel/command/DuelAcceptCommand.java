

package me.array.ArrayPractice.duel.command;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.duel.DuelRequest;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.match.impl.SoloMatch;
import me.array.ArrayPractice.match.impl.TeamMatch;
import me.array.ArrayPractice.match.impl.HCFMatch;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.arena.impl.StandaloneArena;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CPL;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "duel accept" })
public class DuelAcceptCommand
{
    public void execute(final Player player, @CPL("player") final Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "That player is no longer online.");
            return;
        }
        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player while being frozen.");
            return;
        }
        if (target.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player who's frozen.");
            return;
        }
        final Profile senderProfile = Profile.getByUuid(player.getUniqueId());
        if (senderProfile.isBusy(player)) {
            player.sendMessage(CC.RED + "You cannot duel anyone right now.");
            return;
        }
        final Profile receiverProfile = Profile.getByUuid(target.getUniqueId());
        if (!receiverProfile.isPendingDuelRequest(player)) {
            player.sendMessage(CC.RED + "You do not have a pending duel request from " + target.getName() + CC.RED + ".");
            return;
        }
        if (receiverProfile.isBusy(target)) {
            player.sendMessage(CC.translate(CC.RED + target.getDisplayName()) + CC.RED + " is currently busy.");
            return;
        }
        final DuelRequest request = receiverProfile.getSentDuelRequests().get(player.getUniqueId());
        if (request == null) {
            return;
        }
        if (request.isParty()) {
            if (senderProfile.getParty() == null) {
                player.sendMessage(CC.RED + "You do not have a party to duel with.");
                return;
            }
            if (receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player does not have a party to duel with.");
                return;
            }
        }
        else {
            if (senderProfile.getParty() != null) {
                player.sendMessage(CC.RED + "You cannot duel whilst in a party.");
                return;
            }
            if (receiverProfile.getParty() != null) {
                player.sendMessage(CC.RED + "That player is in a party and cannot duel right now.");
                return;
            }
        }
        Arena arena = request.getArena();
        if (arena == null) {
            player.sendMessage(CC.RED + "Tried to start a match but the arena was invalid.");
            return;
        }
        if (arena.isActive()) {
            if (!arena.getType().equals(ArenaType.STANDALONE)) {
                player.sendMessage(CC.RED + "The arena you were dueled was a build match and there were no arenas found.");
                return;
            }
            final StandaloneArena sarena = (StandaloneArena)arena;
        }
        if (!arena.getType().equals(ArenaType.SHARED) && !arena.getType().equals(ArenaType.KOTH)) {
            arena.setActive(true);
        }
        Match match;
        if (request.isParty()) {
            final Team teamA = new Team(new TeamPlayer(player));
            if (request.getKit().getName().equals("HCFDIAMOND")) {
                for (final Player partyMember : senderProfile.getParty().getPlayers()) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }
                final Team teamB = new Team(new TeamPlayer(target));
                for (final Player partyMember2 : receiverProfile.getParty().getPlayers()) {
                    if (!partyMember2.getPlayer().equals(target)) {
                        teamB.getTeamPlayers().add(new TeamPlayer(partyMember2));
                    }
                }
                match = new HCFMatch(teamA, teamB, arena);
            }
            else {
                for (final Player partyMember : senderProfile.getParty().getPlayers()) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }
                final Team teamB = new Team(new TeamPlayer(target));
                for (final Player partyMember2 : receiverProfile.getParty().getPlayers()) {
                    if (!partyMember2.getPlayer().equals(target)) {
                        teamB.getTeamPlayers().add(new TeamPlayer(partyMember2));
                    }
                }
                match = new TeamMatch(teamA, teamB, request.getKit(), arena);
            }
        }
        else {
            match = new SoloMatch(null, new TeamPlayer(player), new TeamPlayer(target), request.getKit(), arena, QueueType.UNRANKED);
        }
        match.start();
    }
}
