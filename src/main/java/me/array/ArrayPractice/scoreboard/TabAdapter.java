package me.array.ArrayPractice.scoreboard;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.ffa.FFA;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.match.team.Team;
import java.util.List;
import me.array.ArrayPractice.event.impl.tournament.Tournament;
import me.array.ArrayPractice.match.team.TeamPlayer;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import me.array.ArrayPractice.profile.Profile;
import java.util.HashSet;
import me.allen.ziggurat.objects.BufferedTabObject;
import java.util.Set;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.CC;
import me.allen.ziggurat.ZigguratAdapter;

public class TabAdapter implements ZigguratAdapter
{
    @Override
    public String getHeader() {
        return "" + CC.translate("&b&lMoonNight Practice");
    }

    @Override
    public String getFooter() {
        return "" + CC.translate("&7store.moonnight.rip");
    }

    @Override
    public Set<BufferedTabObject> getSlots(final Player player) {
        final Set<BufferedTabObject> tabObjects = new HashSet<BufferedTabObject>();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile == null) {
            return tabObjects;
        }
        final List<Integer> takenSlots = new ArrayList<Integer>();
        final int[] ipSlots = { 2, 22, 42, 20, 40, 60 };
        tabObjects.add(new BufferedTabObject().slot(21).text(CC.AQUA + CC.BOLD + "MoonNight " + CC.GRAY + "\u239c" + CC.WHITE + CC.BOLD + " Beta"));
        for (final int ipSlot : ipSlots) {
            tabObjects.add(new BufferedTabObject().text("&7&m----------------").slot(ipSlot));
            takenSlots.add(ipSlot);
        }
        takenSlots.add(0);
        takenSlots.add(1);
        takenSlots.add(3);
        takenSlots.add(21);
        takenSlots.add(23);
        takenSlots.add(41);
        takenSlots.add(43);
        tabObjects.add(new BufferedTabObject().slot(67).text("&c&lWARNING!"));
        tabObjects.add(new BufferedTabObject().slot(68).text("&fPlease use"));
        tabObjects.add(new BufferedTabObject().slot(69).text("&f1.7 for the"));
        tabObjects.add(new BufferedTabObject().slot(70).text("&foptimal gameplay"));
        tabObjects.add(new BufferedTabObject().slot(71).text("&fexperience"));
        if (profile.isInLobby() || profile.isInQueue()) {
            if (!profile.isInTournament(player)) {
                tabObjects.add(new BufferedTabObject().slot(3).text(CC.AQUA + CC.BOLD + "Your Stats"));
                int addedstats = 4;
                for (final Kit kit : Kit.getKits()) {
                    if (kit.isEnabled() && kit.getGameRules().isRanked()) {
                        tabObjects.add(new BufferedTabObject().slot(addedstats).text(CC.WHITE + kit.getName() + ": " + CC.AQUA + profile.getKitData().get(kit).getElo()));
                        if (++addedstats >= 20) {
                            break;
                        }
                        continue;
                    }
                }
                tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Lobby Info"));
                tabObjects.add(new BufferedTabObject().slot(24).text(CC.WHITE + "Online: " + CC.AQUA + Bukkit.getOnlinePlayers().size()));
                tabObjects.add(new BufferedTabObject().slot(25).text(CC.WHITE + "In Queue: " + CC.AQUA + this.getInQueues()));
                tabObjects.add(new BufferedTabObject().slot(26).text(CC.WHITE + "In Fight: " + CC.AQUA + this.getInFights()));
                tabObjects.add(new BufferedTabObject().slot(43).text(CC.AQUA + CC.BOLD + "Party List"));
                if (profile.getParty() != null) {
                    int addedparty = 44;
                    final Party party = profile.getParty();
                    for (final TeamPlayer teamPlayer : party.getTeamPlayers()) {
                        tabObjects.add(new BufferedTabObject().slot(addedparty).text(" &7" + (party.isLeader(teamPlayer.getUuid()) ? "* " : "- ") + CC.WHITE + teamPlayer.getUsername()));
                        if (++addedparty >= 60) {
                            break;
                        }
                    }
                }
            }
            else {
                int added = 4;
                tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Tournament"));
                for (final Tournament.TournamentMatch match : Tournament.CURRENT_TOURNAMENT.getTournamentMatches()) {
                    tabObjects.add(new BufferedTabObject().slot(added).text(CC.AQUA + match.getTeamA().getLeader().getPlayer().getName()));
                    tabObjects.add(new BufferedTabObject().slot(added + 20).text(CC.AQUA + "vs"));
                    tabObjects.add(new BufferedTabObject().slot(added + 40).text(CC.AQUA + match.getTeamB().getLeader().getPlayer().getName()));
                    if (++added >= 20) {
                        break;
                    }
                }
            }
        }
        else if (profile.isInFight()) {
            final Match match2 = profile.getMatch();
            if (match2 != null) {
                if (match2.isSoloMatch()) {
                    final TeamPlayer opponent = match2.getOpponentTeamPlayer(player);
                    tabObjects.add(new BufferedTabObject().slot(4).text("&aYou"));
                    tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Match Info"));
                    tabObjects.add(new BufferedTabObject().slot(44).text("&cEnemy"));
                    tabObjects.add(new BufferedTabObject().slot(5).text(CC.AQUA + player.getName()));
                    tabObjects.add(new BufferedTabObject().slot(24).text(CC.WHITE + "Duration:"));
                    tabObjects.add(new BufferedTabObject().slot(25).text(CC.AQUA + match2.getDuration()));
                    tabObjects.add(new BufferedTabObject().slot(45).text(CC.AQUA + opponent.getUsername()));
                }
                else if (match2.isTeamMatch() || match2.isHCFMatch() || match2.isKoTHMatch()) {
                    final Team team = match2.getTeam(player);
                    final Team opponentTeam = match2.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        tabObjects.add(new BufferedTabObject().slot(4).text("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")"));
                        int added2 = 5;
                        for (final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            tabObjects.add(new BufferedTabObject().slot(added2).text(" " + ((!teamPlayer.isAlive() || teamPlayer.isDisconnected()) ? "&7&m" : "") + teamPlayer.getUsername()));
                            if (++added2 >= 20) {
                                break;
                            }
                        }
                        tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "&lMatch Info"));
                        tabObjects.add(new BufferedTabObject().slot(24).text(CC.WHITE + "Duration:"));
                        tabObjects.add(new BufferedTabObject().slot(25).text(CC.AQUA + match2.getDuration()));
                        int added3 = 45;
                        tabObjects.add(new BufferedTabObject().slot(44).text("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")"));
                        for (final TeamPlayer teamPlayer2 : opponentTeam.getTeamPlayers()) {
                            tabObjects.add(new BufferedTabObject().slot(added3).text(" " + ((!teamPlayer2.isAlive() || teamPlayer2.isDisconnected()) ? "&7&m" : "") + teamPlayer2.getUsername()));
                            if (++added3 >= 60) {
                                break;
                            }
                        }
                    }
                }
                else if (match2.isFreeForAllMatch()) {
                    final Team team = match2.getTeam(player);
                    tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "&lMatch Info"));
                    tabObjects.add(new BufferedTabObject().slot(25).text(CC.WHITE + "Opponents: " + CC.AQUA + team.getAliveCount() + "/" + team.getTeamPlayers().size()));
                    tabObjects.add(new BufferedTabObject().slot(27).text(CC.WHITE + "Duration:"));
                    tabObjects.add(new BufferedTabObject().slot(28).text(CC.AQUA + match2.getDuration()));
                }
            }
        }
        else if (profile.isInBrackets()) {
            final Brackets brackets = profile.getBrackets();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Brackets"));
            int pl = 0;
            for (int added4 = 0; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (brackets.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player bracketsPlayer = brackets.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + bracketsPlayer.getName()));
                }
            }
        }
        else if (profile.isInSumo()) {
            final Sumo sumo = profile.getSumo();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Sumo"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (sumo.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player sumoPlayer = sumo.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + sumoPlayer.getName()));
                }
            }
        }
        else if (profile.isInFfa()) {
            final FFA ffa = profile.getFfa();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "FFA"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (ffa.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player ffaPlayer = ffa.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + ffaPlayer.getName()));
                }
            }
        }
        else if (profile.isInJuggernaut()) {
            final Juggernaut juggernaut = profile.getJuggernaut();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Juggernaut"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (juggernaut.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player juggernautPlayer = juggernaut.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + juggernautPlayer.getName()));
                }
            }
        }
        else if (profile.isInParkour()) {
            final Parkour parkour = profile.getParkour();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Parkour"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (parkour.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player parkourPlayer = parkour.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + parkourPlayer.getName()));
                }
            }
        }
        else if (profile.isInWipeout()) {
            final Wipeout wipeout = profile.getWipeout();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Wipeout"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (wipeout.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player wipeoutPlayer = wipeout.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + wipeoutPlayer.getName()));
                }
            }
        }
        else if (profile.isInSkyWars()) {
            final SkyWars skyWars = profile.getSkyWars();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Spleef"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (skyWars.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player skyWarsPlayer = skyWars.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + skyWarsPlayer.getName()));
                }
            }
        }
        else if (profile.isInSpleef()) {
            final Spleef spleef = profile.getSpleef();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Spleef"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (spleef.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player spleefPlayer = spleef.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + spleefPlayer.getName()));
                }
            }
        }
        else if (profile.isInInfected()) {
            final Infected infected = profile.getInfected();
            tabObjects.add(new BufferedTabObject().slot(23).text(CC.AQUA + CC.BOLD + "Infected"));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!takenSlots.contains(added4)) {
                    if (infected.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player infectedPlayer = infected.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    tabObjects.add(new BufferedTabObject().slot(added4).text(CC.AQUA + infectedPlayer.getName()));
                }
            }
        }
        return tabObjects;
    }

    public int getInQueues() {
        int inQueues = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null && profile.isInQueue()) {
                ++inQueues;
            }
        }
        return inQueues;
    }

    public int getInFights() {
        int inFights = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null && (profile.isInFight() || profile.isInEvent())) {
                ++inFights;
            }
        }
        return inFights;
    }
}