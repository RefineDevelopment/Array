package me.drizzy.practice.duel.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.duel.DuelRequest;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.*;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.enums.ArenaType;
import org.bukkit.entity.Player;

@CommandMeta(label = "duel accept")
public class DuelAcceptCommand {

    public void execute(Player player, @CPL("player") Player target) {
        if (target == null) {
            player.sendMessage(CC.RED + "That player is no longer online.");
            return;
        }

        Profile senderProfile=Profile.getByUuid(player.getUniqueId());

        if (senderProfile.isBusy()) {
            player.sendMessage(CC.RED + "You cannot duel anyone right now.");
            return;
        }

        Profile receiverProfile=Profile.getByUuid(target.getUniqueId());

        if (!receiverProfile.isPendingDuelRequest(player)) {
            player.sendMessage(CC.RED + "You do not have a pending duel request from " + target.getName() + CC.RED + ".");
            return;
        }

        if (receiverProfile.isBusy()) {
            player.sendMessage(CC.translate(CC.RED + target.getDisplayName()) + CC.RED + " is currently busy.");
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
                StandaloneArena sarena=(StandaloneArena) arena;
                if (sarena.getDuplicates() != null) {
                    boolean foundarena=false;
                    for ( Arena darena : sarena.getDuplicates() ) {
                        if (!darena.isActive()) {
                            arena=darena;
                            foundarena=true;
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

        Match match;

        if (request.isParty()) {
            Team teamA=new Team(new TeamPlayer(player));
            if (request.getKit().getName().equals("HCFTeamFight")) {

                for ( Player partyMember : senderProfile.getParty().getPlayers() ) {
                    if (!partyMember.getPlayer().equals(player)) {
                        teamA.getTeamPlayers().add(new TeamPlayer(partyMember));
                    }
                }

                Team teamB=new Team(new TeamPlayer(target));

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

                Team teamB=new Team(new TeamPlayer(target));

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
            for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList()) {
                String opponentMessages=this.formatMessages(string, player.getDisplayName(), target.getDisplayName());
                String message=CC.translate(opponentMessages)
                        .replace("<kit>", match.getKit().getName())
                        .replace("<arena>", request.getArena().getName());
                match.broadcastMessage(message);
            }
        }
        match.start();
    }

    private String formatMessages(String string, String player1, String player2) {
        String player1Format;
        String player2Format;
        player1Format = player1;
        player2Format = player2;
        string = string.replace("<player1>", player1Format).replace("<player2>", player2Format);
        return string;
    }


}
