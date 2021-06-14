package xyz.refinedev.practice.cmds.event;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/25/2021
 * Project: Array
 */

public class TournamentCommands {

    /**
     *
     * ALL THE COMMAND CODE IS VERY BAD AND THIS IS GOING TO BE RECODED IN 2.1
     *
     */


    @Command(name = "", aliases = "help", desc = "View Tournament Commands")
    public void help(@Sender CommandSender player) {
        Locale.TOURNAMENT_HELP.toList().forEach(player::sendMessage);
    }

    @Command(name = "host", aliases = "start", usage = "<1/2> <kit>", desc = "Host a Tournament with Type and Kit")
    @Require("array.host.tournament")
    public void host(@Sender Player player, int size, Kit kit) {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            player.sendMessage(ChatColor.RED + "The Tournament has already started");
            return;
        }

        Tournament.CURRENT_TOURNAMENT = new Tournament();

        if (size == 1 || size == 2) {
            Tournament.CURRENT_TOURNAMENT.setTeamCount(size);
        } else {
            player.sendMessage(ChatColor.RED + "Please choose 1 or 2");
            Tournament.CURRENT_TOURNAMENT.cancel();
            return;
        }

        Tournament.CURRENT_TOURNAMENT.setLadder(kit);

        List<String> strings = new ArrayList<>();

        strings.add(Locale.TOURNAMENT_BROADCAST.toString()
                .replace("<host_name>", player.getDisplayName())
                .replace("<kit>", kit.getDisplayName())
                .replace("<tournament_type>", size == 1 ? "1v1" : "2v2"));

        strings.add(Locale.TOURNAMENT_ACCEPT.toString());

        strings.forEach(string -> new Clickable(string, Locale.TOURNAMENT_HOVER.toString(), "/tournament join"));

        Tournament.RUNNABLE = new BukkitRunnable() {
            private int countdown = 60;

            @Override
            public void run() {
                countdown--;
                if (countdown == 30 || countdown == 10 || countdown <= 3) {
                    if (countdown > 0) {
                        strings.forEach(string -> new Clickable(string, Locale.TOURNAMENT_HOVER.toString(), "/tournament join"));
                    }
                }
                if (countdown <= 0) {
                    Tournament.RUNNABLE = null;
                    cancel();
                    if (Tournament.CURRENT_TOURNAMENT.getParticipatingCount() < 2) {
                        Bukkit.broadcastMessage(Locale.TOURNAMENT_CANCELLED.toString());
                        Tournament.CURRENT_TOURNAMENT.cancel();
                    } else {
                        Tournament.CURRENT_TOURNAMENT.tournamentstart();
                    }
                }
            }
        };
        Tournament.RUNNABLE.runTaskTimer(Array.getInstance(), 20, 20);
    }

    @Command(name = "cancel", aliases = "abort", desc = "Cancel the current on-going tournament")
    @Require("array.tournament.admin")
    public void cancel(@Sender Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null) {
            player.sendMessage(ChatColor.RED + "There is no active tournament currently.");
            return;
        }
        if (Tournament.RUNNABLE != null) {
            Tournament.RUNNABLE.cancel();
        }
        Tournament.CURRENT_TOURNAMENT.cancel();
        Tournament.CURRENT_TOURNAMENT = null;
        Bukkit.broadcastMessage(Locale.TOURNAMENT_CANCELLED.toString());
    }


    @Command(name = "join", aliases = "participate", desc = "Join the current on-going tournament")
    public void join(@Sender Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null || Tournament.CURRENT_TOURNAMENT.hasStarted()) {
            player.sendMessage(ChatColor.RED + "There isn't a joinable Tournament");
            return;
        }
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (Tournament.CURRENT_TOURNAMENT.getTeamCount() == 1) {
            Party party = profile.getParty();
            if (party != null && party.getPlayers().size() != 1) {
                player.sendMessage("This is a solo Tournament");
                return;
            }
        } else {
            Party party = Profile.getByUuid(player.getUniqueId()).getParty();
            if (party == null || party.getPlayers().size() != Tournament.CURRENT_TOURNAMENT.getTeamCount()) {
                player.sendMessage(ChatColor.RED + "The Tournament needs " + Tournament.CURRENT_TOURNAMENT.getTeamCount() + " players to start.");
                return;
            }
            if (!party.isLeader(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Only Leaders can do this");
                return;
            }
        }
        if (profile.isBusy()) {
            player.sendMessage(ChatColor.RED + "You cannot join the Tournament in your current state");
            return;
        }
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            player.chat("/party create");
            party = Profile.getByUuid(player.getUniqueId()).getParty();
        }
        if (Tournament.CURRENT_TOURNAMENT.getParticipants().contains(party)) {
            player.sendMessage(CC.translate("&7You are already in the tournament"));
            return;
        }
        Tournament.CURRENT_TOURNAMENT.participate(party);
    }

    @Command(name = "leave", aliases = "exit", desc = "Leave the current on-going tournament")
    public void leave(@Sender Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null || Tournament.CURRENT_TOURNAMENT.hasStarted()) {
            player.sendMessage(ChatColor.RED + "There isn't a Tournament you can leave");
            return;
        }
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            player.sendMessage("You aren't currently in a Tournament");
            return;
        }
        if (!Tournament.CURRENT_TOURNAMENT.isParticipating(player)) {
            player.sendMessage("You aren't currently in a Tournament");
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "&cOnly Leaders can do this");
            return;
        }
        Tournament.CURRENT_TOURNAMENT.leave(party);
    }


    @Command(name = "list", aliases = "info", desc = "View Information about the on-going Tournament")
    public void list(@Sender Player player) {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            Tournament tournament = Tournament.CURRENT_TOURNAMENT;
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.BLUE).append("Tournament ").append(tournament.getTeamCount()).append("v").append(tournament.getTeamCount()).append("'s matches:");
            builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
            builder.append(CC.RED).append("Ladder: ").append(ChatColor.WHITE).append(tournament.getLadder().getName()).append("\n");
            builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
            for ( Tournament.TournamentMatch match : tournament.getTournamentMatches()) {
                String teamANames = match.getTeamA().getLeader().getPlayer().getName();
                String teamBNames = match.getTeamB().getLeader().getPlayer().getName();
                builder.append(ChatColor.YELLOW).append(teamANames).append("'s Party").append(ChatColor.WHITE).append(" vs. ").append(ChatColor.RED).append(teamBNames).append("'s Party").append("\n");
            }
            builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
            builder.append(CC.RED).append("Round: ").append(ChatColor.WHITE).append(tournament.getRound());
            builder.append("\n");
            builder.append(CC.RED).append("Players: ").append(ChatColor.WHITE).append(tournament.getParticipatingCount()).append("\n");
            player.sendMessage(builder.toString());
        } else {
            player.sendMessage(ChatColor.BLUE + "There aren't any active Tournaments");
        }
    }

}
