package me.drizzy.practice.tablist;

import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.gulag.Gulag;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.*;

public class Tab implements ZigguratAdapter {

    @Override
    public String getHeader() {
        return "" + CC.translate(Array.getInstance().getMainConfig().getString("Tab.Header"));
    }

    @Override
    public String getFooter() {
        return "" + CC.translate(Array.getInstance().getMainConfig().getString("Tab.Footer"));
    }

    @Override
    public Set<BufferedTabObject> getSlots(final Player player) {
        final Set<BufferedTabObject> elements = new HashSet<>();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final List<Integer> tabSlots = new ArrayList<>();
        if (!profile.getSettings().isVanillaTab()) {
        final int[] ipSlots = { 2, 22, 42, 20, 40, 60 };
            elements.add(new BufferedTabObject().slot(21).text("&b&lPractice &7| &f&lEU"));
        for (final int ipSlot : ipSlots) {
            elements.add(new BufferedTabObject().slot(19).text("&7store.purge.com"));
            elements.add(new BufferedTabObject().slot(59).text("&7discord.purge.com"));
            elements.add(new BufferedTabObject().slot(39).text("&7www.purge.com"));
            elements.add(new BufferedTabObject().slot(ipSlot).text("&7&m----------------"));
            tabSlots.add(ipSlot);
        }
        tabSlots.add(0);
        tabSlots.add(1);
        tabSlots.add(3);
        tabSlots.add(21);
        tabSlots.add(23);
        tabSlots.add(41);
        tabSlots.add(43);
        elements.add(new BufferedTabObject().slot(67).text("&b&lIMPORTANT NOTICE"));
        elements.add(new BufferedTabObject().slot(68).text("&fPlease use"));
        elements.add(new BufferedTabObject().slot(69).text("&fversion &b&l1.7"));
        elements.add(new BufferedTabObject().slot(70).text("&ffor optimal"));
        elements.add(new BufferedTabObject().slot(71).text("&fgameplay experience"));
        if (profile.isInLobby() || profile.isInQueue()) {
            if (!profile.isInTournament(player)) {
                elements.add(new BufferedTabObject().slot(3).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Your Stats"));
                int statslots = 4;
                for (final Kit kit : Kit.getKits()) {
                    if (kit.isEnabled() && kit.getGameRules().isRanked()) {
                        elements.add(new BufferedTabObject().slot(statslots).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + kit.getName() + ": " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + profile.getStatisticsData().get(kit).getElo()));
                        if (++statslots >= 20) {
                            break;
                        }
                    }
                }
                elements.add(new BufferedTabObject().slot(23).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Lobby Info"));
                elements.add(new BufferedTabObject().slot(24).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Online: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ArrayCache.getOnline()));
                elements.add(new BufferedTabObject().slot(25).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "In Queue: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ArrayCache.getInQueues()));
                elements.add(new BufferedTabObject().slot(26).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "In Fight: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ArrayCache.getInFights()));
                elements.add(new BufferedTabObject().slot(43).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Party List"));
                if (profile.getParty() != null) {
                    int partyslots = 44;
                    final Party party = profile.getParty();
                    for (final TeamPlayer teamPlayer : party.getTeamPlayers()) {
                        elements.add(new BufferedTabObject().slot(partyslots).text(" &7" + (party.isLeader(teamPlayer.getUuid()) ? "* " : "- ") + Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + teamPlayer.getUsername()));
                        if (++partyslots >= 60) {
                            break;
                        }
                    }
                }
            } else if (profile.isInTournament(player)) {
                int added = 4;
                elements.add(new BufferedTabObject().slot(23).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Tournament"));
                for (final Tournament.TournamentMatch match : Tournament.CURRENT_TOURNAMENT.getTournamentMatches()) {
                    int pl = 0;
                    for (int added4 = 4; added4 < 60; ++added4) {
                        if (!tabSlots.contains(added4)) {
                            if (Tournament.CURRENT_TOURNAMENT.getParticipants().size() <= pl) {
                                break;
                            }
                            final Player tournamentPlayer = Tournament.CURRENT_TOURNAMENT.getParticipants().get(pl).getLeader().getPlayer();
                            ++pl;
                            elements.add(new BufferedTabObject().slot(added4).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + tournamentPlayer.getName() + "'s Party"));
                        }
                    }
                    if (match.isStarting()) {
                        elements.add(new BufferedTabObject().slot(added).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match.getTeamA().getLeader().getPlayer().getName()));
                        elements.add(new BufferedTabObject().slot(added + 20).text("&7vs"));
                        elements.add(new BufferedTabObject().slot(added + 40).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match.getTeamB().getLeader().getPlayer().getName()));
                        if (++added >= 20) {
                            break;
                        }
                    }
                }
            }
        }
        else if (profile.isInFight()) {
            final Match match2 = profile.getMatch();
            if (match2 != null) {
                if (match2.isSoloMatch() || match2.isSumoMatch() || match2.isTheBridgeMatch()) {
                    final TeamPlayer opponent = match2.getOpponentTeamPlayer(player);
                    elements.add(new BufferedTabObject().text("&a&lYou").slot(4));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Match Details").slot(23));
                    elements.add(new BufferedTabObject().text("&c&lEnemy").slot(44));
                    elements.add(new BufferedTabObject().text("&a" + player.getName()).slot(5));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration: &b" + match2.getDuration()).slot(24));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Arena: &b" + match2.getArena().getName()).slot(25));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Kit: &b" + match2.getKit().getName()).slot(26));
                    elements.add(new BufferedTabObject().text("&c" + opponent.getUsername()).slot(45));
                }
                else if (match2.isTeamMatch() || match2.isSumoTeamMatch()) {
                    final Team team = match2.getTeam(player);
                    final Team opponentTeam = match2.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        elements.add(new BufferedTabObject().text("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")").slot(4));
                        int added2 = 5;
                        for (final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer.isAlive() || teamPlayer.isDisconnected()) ? "&7&m" : "") + teamPlayer.getUsername()).slot(added2));
                            if (++added2 >= 20) {
                                break;
                            }
                        }
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "&lMatch Info").slot(23));
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration: &b" + match2.getDuration()).slot(24));
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Arena: &b" + match2.getArena().getName()).slot(25));
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Kit: &b" + match2.getKit().getName()).slot(26));
                        int added3 = 45;
                        elements.add(new BufferedTabObject().text("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")").slot(44));
                        for (final TeamPlayer teamPlayer2 : opponentTeam.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer2.isAlive() || teamPlayer2.isDisconnected()) ? "&7&m" : "") + teamPlayer2.getUsername()).slot(added3));
                            if (++added3 >= 60) {
                                break;
                            }
                        }
                    }
                    if (match2.isSumoTeamMatch()) {
                        elements.add(new BufferedTabObject().text("&b&lPoints").slot(27));
                        elements.add(new BufferedTabObject().text("&aTeam &8- &f" + team.getSumoRounds()).slot(8));
                        elements.add(new BufferedTabObject().text("&cOpponents &8- &f" + opponentTeam.getSumoRounds()).slot(28));

                    }
                } else if (match2.isHCFMatch()) {
                    final Team team = match2.getTeam(player);
                    final Team opponentTeam = match2.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        elements.add(new BufferedTabObject().text("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")").slot(4));
                        int added2 = 5;
                        for (final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer.isAlive() || teamPlayer.isDisconnected()) ? "&7&m" : "") + teamPlayer.getUsername()).slot(added2));
                            if (++added2 >= 20) {
                                break;
                            }
                        }
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "&lMatch Info").slot(23));
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration: &b" + match2.getDuration()).slot(24));
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Arena: &b" + match2.getArena().getName()).slot(25));
                        int added3 = 45;
                        elements.add(new BufferedTabObject().text("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")").slot(44));
                        for (final TeamPlayer teamPlayer2 : opponentTeam.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer2.isAlive() || teamPlayer2.isDisconnected()) ? "&7&m" : "") + teamPlayer2.getUsername()).slot(added3));
                            if (++added3 >= 60) {
                                break;
                            }
                        }
                    }
                }
                else if (match2.isFreeForAllMatch()) {
                    final Team team = match2.getTeam(player);
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "&lMatch Info").slot(23));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Opponents: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + team.getAliveCount() + "/" + team.getTeamPlayers().size()).slot(25));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match2.getDuration()).slot(27));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Arena: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match2.getArena().getDisplayName()).slot(27));
                }
            }
        }
        else if (profile.isInBrackets()) {
            final Brackets brackets = profile.getBrackets();
            elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Brackets").slot(23));
            int pl = 0;
            for (int added4 = 0; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (brackets.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player bracketsPlayer = brackets.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + bracketsPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInGulag()) {
            final Gulag gulag = profile.getGulag();
            elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Gulag").slot(23));
            int pl = 0;
            for (int added4 = 0; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (gulag.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player gulagPlayer = gulag.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + gulagPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInSumo()) {
            final Sumo sumo = profile.getSumo();
            elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Sumo").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (sumo.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player sumoPlayer = sumo.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + sumoPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInLMS()) {
            final LMS ffa = profile.getLms();
            elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "LMS").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (ffa.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player ffaPlayer = ffa.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ffaPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInParkour()) {
            final Parkour parkour = profile.getParkour();
            elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Parkour").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (parkour.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player parkourPlayer = parkour.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + parkourPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInSpleef()) {
            final Spleef spleef = profile.getSpleef();
            elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Spleef").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (spleef.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player spleefPlayer = spleef.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + spleefPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isSpectating()) {
            final Match match2=profile.getMatch();
            if (match2 != null) {
                if (match2.isSoloMatch()) {
                    elements.add(new BufferedTabObject().text("&aTeam A").slot( 4));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Match Info").slot( 23));
                    elements.add(new BufferedTabObject().text("&aTeam B").slot( 44));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match2.getTeamPlayerA().getUsername()).slot( 5));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration:").slot( 24));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + match2.getDuration()).slot( 25));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + match2.getTeamPlayerB().getUsername()).slot( 45));
                } else if (match2.isTeamMatch() || match2.isHCFMatch()) {
                    elements.add(new BufferedTabObject().text("&aTeam A").slot( 4));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Match Info").slot( 23));
                    elements.add(new BufferedTabObject().text("&aTeam B").slot( 44));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match2.getTeamA().getLeader().getUsername()).slot( 5));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration:").slot( 24));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + match2.getDuration()).slot( 25));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + match2.getTeamB().getLeader().getUsername()).slot( 45));
                } else if (match2.isFreeForAllMatch()) {
                    final int team=match2.getPlayers().size();
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "FFA Match Info").slot( 23));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Players: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + team).slot( 25));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + "Duration:").slot( 27));
                    elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + match2.getDuration()).slot( 28));
                }
            }
        }
    } else {
        int pl=0;
        for ( int added4=1; added4 < 60; ++added4 ) {
            elements.add(new BufferedTabObject().text("&b&lPractice &7| &f&lEU").slot(21));
            elements.add(new BufferedTabObject().text("&7store.purgemc.club").slot(40));
            elements.add(new BufferedTabObject().text("&7discord.purgemc.club").slot(60));
            elements.add(new BufferedTabObject().text("&7www.purgemc.club").slot(20));
            tabSlots.add(0);
            tabSlots.add(1);
            tabSlots.add(21);
            tabSlots.add(23);
            tabSlots.add(41);
            tabSlots.add(43);
            elements.add(new BufferedTabObject().slot(67).text("&b&lIMPORTANT NOTICE"));
            elements.add(new BufferedTabObject().slot(68).text("&fPlease use"));
            elements.add(new BufferedTabObject().slot(69).text("&fversion &b&l1.7"));
            elements.add(new BufferedTabObject().slot(70).text("&ffor optimal"));
            elements.add(new BufferedTabObject().slot(71).text("&fgameplay experience"));
            if (!tabSlots.contains(added4)) {
                if (Profile.getPlayerList().size() <= pl) {
                    break;
                }
                Player player1 = Profile.getPlayerList().get(pl).getPlayer();
                ++pl;
                elements.add(new BufferedTabObject().text(ChatColor.GREEN + player1.getName()).slot(added4).ping(player1.spigot().getPing()));
            }
        }
    }
        return elements;
    }
}