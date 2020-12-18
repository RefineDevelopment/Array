package me.array.ArrayPractice.tab;

import cc.outlast.tablist.ITablist;
import cc.outlast.tablist.TablistElement;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.match.team.Team;
import java.util.List;
import me.array.ArrayPractice.tournament.Tournament;
import me.array.ArrayPractice.match.team.TeamPlayer;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.CC;

public class Tab implements ITablist {
    
    @Override
    public String getHeader(Player player) {
        return "" + CC.translate("&b&lMoonNight Practice");
    }

    @Override
    public String getFooter(Player player) {
        return "" + CC.translate("&7store.moonnight.rip");
    }

    @Override
    public List<TablistElement> getElements(final Player player) {
        final List<TablistElement> elements = new ArrayList<>();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final List<Integer> tabSlots = new ArrayList<>();
        final int[] ipSlots = { 2, 22, 42, 20, 40, 60 };
        elements.add(new TablistElement(CC.AQUA + CC.BOLD + "MoonNight " + CC.GRAY + "\u239c" + CC.WHITE + CC.BOLD + " Beta",21));
        for (final int ipSlot : ipSlots) {
            elements.add(new TablistElement("&7&m----------------", ipSlot));
            tabSlots.add(ipSlot);
        }
        tabSlots.add(0);
        tabSlots.add(1);
        tabSlots.add(3);
        tabSlots.add(21);
        tabSlots.add(23);
        tabSlots.add(41);
        tabSlots.add(43);
        elements.add(new TablistElement("&c&lWARNING!",67));
        elements.add(new TablistElement("&fPlease use",68));
        elements.add(new TablistElement("&f1.7 for the",69));
        elements.add(new TablistElement("&foptimal gameplay",70));
        elements.add(new TablistElement("&fexperience",71));
        if (profile.isInLobby() || profile.isInQueue()) {
            if (!profile.isInTournament(player)) {
                elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Your Stats",3));
                int addedstats = 4;
                for (final Kit kit : Kit.getKits()) {
                    if (kit.isEnabled() && kit.getGameRules().isRanked()) {
                        elements.add(new TablistElement(CC.WHITE + kit.getName() + ": " + CC.AQUA + profile.getKitData().get(kit).getElo(),addedstats));
                        if (++addedstats >= 20) {
                            break;
                        }
                    }
                }
                elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Lobby Info",23));
                elements.add(new TablistElement(CC.WHITE + "Online: " + CC.AQUA + Bukkit.getOnlinePlayers().size(),24));
                elements.add(new TablistElement(CC.WHITE + "In Queue: " + CC.AQUA + this.getInQueues(),25));
                elements.add(new TablistElement(CC.WHITE + "In Fight: " + CC.AQUA + this.getInFights(),26));
                elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Party List",43));
                if (profile.getParty() != null) {
                    int addedparty = 44;
                    final Party party = profile.getParty();
                    for (final TeamPlayer teamPlayer : party.getTeamPlayers()) {
                        elements.add(new TablistElement(" &7" + (party.isLeader(teamPlayer.getUuid()) ? "* " : "- ") + CC.WHITE + teamPlayer.getUsername(),addedparty));
                        if (++addedparty >= 60) {
                            break;
                        }
                    }
                }
            }
            else {
                int added = 4;
                elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Tournament",23));
                for (final Tournament.TournamentMatch match : Tournament.CURRENT_TOURNAMENT.getTournamentMatches()) {
                    elements.add(new TablistElement(CC.AQUA + match.getTeamA().getLeader().getPlayer().getName(),added));
                    elements.add(new TablistElement(CC.AQUA + "vs",added + 20));
                    elements.add(new TablistElement(CC.AQUA + match.getTeamB().getLeader().getPlayer().getName(),added + 40));
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
                    elements.add(new TablistElement("&aYou",4));
                    elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Match Info",23));
                    elements.add(new TablistElement("&cEnemy",44));
                    elements.add(new TablistElement(CC.AQUA + player.getName(),5));
                    elements.add(new TablistElement(CC.WHITE + "Duration:",24));
                    elements.add(new TablistElement(CC.AQUA + match2.getDuration(),25));
                    elements.add(new TablistElement(CC.AQUA + opponent.getUsername(),45));
                }
                else if (match2.isTeamMatch() || match2.isHCFMatch() || match2.isKoTHMatch()) {
                    final Team team = match2.getTeam(player);
                    final Team opponentTeam = match2.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        elements.add(new TablistElement("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")",4));
                        int added2 = 5;
                        for (final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            elements.add(new TablistElement(" " + ((!teamPlayer.isAlive() || teamPlayer.isDisconnected()) ? "&7&m" : "") + teamPlayer.getUsername(),added2));
                            if (++added2 >= 20) {
                                break;
                            }
                        }
                        elements.add(new TablistElement(CC.AQUA + CC.BOLD + "&lMatch Info",23));
                        elements.add(new TablistElement(CC.WHITE + "Duration:",24));
                        elements.add(new TablistElement(CC.AQUA + match2.getDuration(),25));
                        int added3 = 45;
                        elements.add(new TablistElement("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")",44));
                        for (final TeamPlayer teamPlayer2 : opponentTeam.getTeamPlayers()) {
                            elements.add(new TablistElement(" " + ((!teamPlayer2.isAlive() || teamPlayer2.isDisconnected()) ? "&7&m" : "") + teamPlayer2.getUsername(),added3));
                            if (++added3 >= 60) {
                                break;
                            }
                        }
                    }
                }
                else if (match2.isFreeForAllMatch()) {
                    final Team team = match2.getTeam(player);
                    elements.add(new TablistElement(CC.AQUA + CC.BOLD + "&lMatch Info",23));
                    elements.add(new TablistElement(CC.WHITE + "Opponents: " + CC.AQUA + team.getAliveCount() + "/" + team.getTeamPlayers().size(),25));
                    elements.add(new TablistElement(CC.WHITE + "Duration:",27));
                    elements.add(new TablistElement(CC.AQUA + match2.getDuration(),28));
                }
            }
        }
        else if (profile.isInBrackets()) {
            final Brackets brackets = profile.getBrackets();
            elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Brackets",23));
            int pl = 0;
            for (int added4 = 0; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (brackets.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player bracketsPlayer = brackets.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new TablistElement(CC.AQUA + bracketsPlayer.getName(),added4));
                }
            }
        }
        else if (profile.isInSumo()) {
            final Sumo sumo = profile.getSumo();
            elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Sumo",23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (sumo.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player sumoPlayer = sumo.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new TablistElement(CC.AQUA + sumoPlayer.getName(),added4));
                }
            }
        }
        else if (profile.isInFfa()) {
            final FFA ffa = profile.getFfa();
            elements.add(new TablistElement(CC.AQUA + CC.BOLD + "FFA",23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (ffa.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player ffaPlayer = ffa.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new TablistElement(CC.AQUA + ffaPlayer.getName(),added4));
                }
            }
        }
        else if (profile.isInParkour()) {
            final Parkour parkour = profile.getParkour();
            elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Parkour",23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (parkour.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player parkourPlayer = parkour.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new TablistElement(CC.AQUA + parkourPlayer.getName(),added4));
                }
            }
        }
        else if (profile.isInSpleef()) {
            final Spleef spleef = profile.getSpleef();
            elements.add(new TablistElement(CC.AQUA + CC.BOLD + "Spleef",23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (spleef.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player spleefPlayer = spleef.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new TablistElement(CC.AQUA + spleefPlayer.getName(),added4));
                }
            }
        }
        return elements;
    }

    public int getInQueues() {
        int inQueues = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInQueue()) {
                ++inQueues;
            }
        }
        return inQueues;
    }

    public int getInFights() {
        int inFights = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInFight() || profile.isInEvent()) {
                ++inFights;
            }
        }
        return inFights;
    }
}