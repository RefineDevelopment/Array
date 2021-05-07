package me.drizzy.practice.tablist;

import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import me.allen.ziggurat.objects.SkinTexture;
import me.drizzy.practice.Array;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.managers.TabManager;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.Skin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tab implements ZigguratAdapter {

    private final TabManager tabManager = Array.getInstance().getTabManager();
    private final Essentials essentials = Array.getInstance().getEssentials();

    @Override
    public String getHeader() {
        return "" + CC.translate(tabManager.getHeader());
    }

    @Override
    public String getFooter() {
        return "" + CC.translate(tabManager.getFooter());
    }

    @Override
    public Set<BufferedTabObject> getSlots(final Player player) {
        final Set<BufferedTabObject> elements = new HashSet<>();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final List<Integer> tabSlots = new ArrayList<>();
        if (!profile.getSettings().isVanillaTab()) {
        final int[] lineSlots = {2, 22, 42};
        elements.add(new BufferedTabObject().slot(21).text(tabManager.getMainColor() + tabManager.getLegacyHeader()));
        for (final int lineSlot : lineSlots) {
            elements.add(new BufferedTabObject().slot(19).text("&b&lTwitter").skin(new SkinTexture(Skin.TWITTER_SKIN.getProperty().getValue(), Skin.TWITTER_SKIN.getProperty().getSignature())));
            elements.add(new BufferedTabObject().slot(20).text(tabManager.getSecondaryColor() + essentials.socialMeta.getTwitter()));
            elements.add(new BufferedTabObject().slot(59).text("&9&lDiscord").skin(new SkinTexture(Skin.DISCORD_SKIN.getProperty().getValue(), Skin.DISCORD_SKIN.getProperty().getSignature())));
            elements.add(new BufferedTabObject().slot(60).text(tabManager.getSecondaryColor() + essentials.socialMeta.getDiscord()));
            elements.add(new BufferedTabObject().slot(39).text("&a&lWebsite").skin(new SkinTexture(Skin.WEBSITE_SKIN.getProperty().getValue(), Skin.WEBSITE_SKIN.getProperty().getSignature())));
            elements.add(new BufferedTabObject().slot(40).text(tabManager.getSecondaryColor() + essentials.socialMeta.getWebiste()));
            elements.add(new BufferedTabObject().slot(lineSlot).text("&7&m----------------"));
            tabSlots.add(lineSlot);
        }
        tabSlots.add(0);
        tabSlots.add(1);
        tabSlots.add(3);
        tabSlots.add(21);
        tabSlots.add(23);
        tabSlots.add(41);
        tabSlots.add(43);
        elements.add(new BufferedTabObject().slot(67).text(tabManager.getMainColor() + "&lIMPORTANT NOTICE"));
        elements.add(new BufferedTabObject().slot(68).text(tabManager.getSecondaryColor() + "Please use"));
        elements.add(new BufferedTabObject().slot(69).text(tabManager.getSecondaryColor() + "version " + tabManager.getMainColor() + "&l1.7"));
        elements.add(new BufferedTabObject().slot(70).text(tabManager.getSecondaryColor() + "for optimal"));
        elements.add(new BufferedTabObject().slot(71).text(tabManager.getSecondaryColor() + "gameplay experience"));
        if (profile.isInLobby() || profile.isInQueue()) {
            if (!profile.isInTournament()) {
                elements.add(new BufferedTabObject().slot(3).text(tabManager.getMainColor() + "&lYour Stats"));
                int statslots = 4;
                for (final Kit kit : Kit.getKits()) {
                    if (kit.isEnabled() && kit.getGameRules().isRanked()) {
                        elements.add(new BufferedTabObject().skin(new SkinTexture(Skin.getDot(tabManager.getDotColor()).getProperty().getValue(), Skin.getDot(tabManager.getDotColor()).getProperty().getSignature())).text(tabManager.getSecondaryColor() + kit.getName() + ": " + tabManager.getMainColor() + profile.getStatisticsData().get(kit).getElo()).slot(statslots));
                        if (++statslots >= 20) {
                            break;
                        }
                    }
                }
                elements.add(new BufferedTabObject().slot(23).text(tabManager.getMainColor() + "&lLobby Info"));
                elements.add(new BufferedTabObject().slot(24).text(tabManager.getSecondaryColor() + " Online: " + tabManager.getMainColor() + ArrayCache.getOnline()));
                elements.add(new BufferedTabObject().slot(25).text(tabManager.getSecondaryColor() + " In Queue: " + tabManager.getMainColor() + ArrayCache.getInQueues()));
                elements.add(new BufferedTabObject().slot(26).text(tabManager.getSecondaryColor() + " In Fight: " + tabManager.getMainColor() + ArrayCache.getInFights()));
                if (profile.getParty() != null) {
                    elements.add(new BufferedTabObject().slot(43).text(tabManager.getMainColor() + "&lYour Party"));
                    int partyslots = 44;
                    final Party party = profile.getParty();
                    for (final TeamPlayer teamPlayer : party.getTeamPlayers()) {
                        elements.add(new BufferedTabObject().slot(partyslots).text(" &7" + (party.isLeader(teamPlayer.getUuid()) ? "* " : "- ") + tabManager.getSecondaryColor() + teamPlayer.getUsername()));
                        if (++partyslots >= 60) {
                            break;
                        }
                    }
                } else {
                    elements.add(new BufferedTabObject().slot(43).text(tabManager.getMainColor() + "You are not in").skin(new SkinTexture(Skin.DEFAULT_SKIN.getProperty().getValue(), Skin.DEFAULT_SKIN.getProperty().getSignature())));
                    elements.add(new BufferedTabObject().slot(44).text(tabManager.getMainColor() + "a party!"));
                    elements.add(new BufferedTabObject().slot(46).text("&7You can create a"));
                    elements.add(new BufferedTabObject().slot(47).text("&7by using"));
                    elements.add(new BufferedTabObject().slot(48).text("&f/party create"));
                }
            } else if (profile.isInTournament()) {
                int added = 4;
                elements.add(new BufferedTabObject().slot(23).text(tabManager.getMainColor() + "&lTournament"));
                for (final Tournament.TournamentMatch match : Tournament.CURRENT_TOURNAMENT.getTournamentMatches()) {
                    int pl = 0;
                    for (int added4 = 4; added4 < 60; ++added4) {
                        if (!tabSlots.contains(added4)) {
                            if (Tournament.CURRENT_TOURNAMENT.getParticipants().size() <= pl) {
                                break;
                            }
                            final Player tournamentPlayer = Tournament.CURRENT_TOURNAMENT.getParticipants().get(pl).getLeader().getPlayer();
                            ++pl;
                            elements.add(new BufferedTabObject().slot(added4).text(tabManager.getMainColor() + tournamentPlayer.getName() + "'s Party"));
                        }
                    }
                    if (match.isStarting()) {
                        elements.add(new BufferedTabObject().slot(added).text(tabManager.getMainColor() + match.getTeamA().getLeader().getPlayer().getName()));
                        elements.add(new BufferedTabObject().slot(added + 20).text("&7vs"));
                        elements.add(new BufferedTabObject().slot(added + 40).text(tabManager.getMainColor() + match.getTeamB().getLeader().getPlayer().getName()));
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
                if (match2.isSoloMatch() || match2.isTheBridgeMatch()) {
                    final TeamPlayer opponent = match2.getOpponentTeamPlayer(player);
                    elements.add(new BufferedTabObject().text("&a&lYou").slot(4).skin(new SkinTexture(Skin.getPlayer(player).getProperty().getValue(), Skin.getPlayer(player).getProperty().getSignature())));
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lMatch Details").slot(23));
                    elements.add(new BufferedTabObject().text("&c&lEnemy").slot(44).skin(new SkinTexture(Skin.getPlayer(opponent.getPlayer()).getProperty().getValue(), Skin.getPlayer(opponent.getPlayer()).getProperty().getSignature())));
                    elements.add(new BufferedTabObject().text("&a" + player.getName()).slot(5));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration: " + tabManager.getMainColor() + "" + match2.getDuration()).slot(24));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Arena: " + tabManager.getMainColor() + "" + match2.getArena().getName()).slot(25));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Kit: " + tabManager.getMainColor() + "" + match2.getKit().getName()).slot(26));
                    elements.add(new BufferedTabObject().text("&c" + opponent.getUsername()).slot(45));

                    if (match2.isTheBridgeMatch() && match2.getOpponentPlayer(player) != null) {
                        elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lPoints").slot(28));
                        Profile profile1 = Profile.getByPlayer(match2.getOpponentPlayer(player));
                        elements.add(new BufferedTabObject().text("&aYour Points (&f" + profile.getBridgeRounds() + "&a)").slot(9));
                        elements.add(new BufferedTabObject().text("&cTheir Pioints (&f" + profile1.getBridgeRounds() + "&c)").slot(49));
                    }
                }
                else if (match2.isTeamMatch()) {
                    final Team team = match2.getTeam(player);
                    final Team opponentTeam = match2.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        elements.add(new BufferedTabObject().text("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")").slot(4));
                        int added2 = 5;
                        for (final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer.isAlive() || teamPlayer.isDisconnected()) ? "&7&m" : "") + teamPlayer.getUsername()).slot(added2).skin(new SkinTexture(Skin.getPlayer(teamPlayer.getPlayer()).getProperty().getValue(), Skin.getPlayer(teamPlayer.getPlayer()).getProperty().getSignature())));
                            if (++added2 >= 20) {
                                break;
                            }
                        }
                        elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lMatch Info").slot(23));
                        elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration: " + tabManager.getMainColor() + "" + match2.getDuration()).slot(24));
                        elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Arena: " + tabManager.getMainColor() + "" + match2.getArena().getName()).slot(25));
                        elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Kit: " + tabManager.getMainColor() + "" + match2.getKit().getName()).slot(26));
                        int added3 = 45;
                        elements.add(new BufferedTabObject().text("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")").slot(44));
                        for (final TeamPlayer teamPlayer2 : opponentTeam.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer2.isAlive() || teamPlayer2.isDisconnected()) ? "&7&m" : "") + teamPlayer2.getUsername()).slot(added3).skin(new SkinTexture(Skin.getPlayer(teamPlayer2.getPlayer()).getProperty().getValue(), Skin.getPlayer(teamPlayer2.getPlayer()).getProperty().getSignature())));
                            if (++added3 >= 60) {
                                break;
                            }
                        }
                    }
                } else if (match2.isHCFMatch()) {
                    final Team team = match2.getTeam(player);
                    final Team opponentTeam = match2.getOpponentTeam(player);
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 30) {
                        elements.add(new BufferedTabObject().text("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")").slot(4));
                        int added2 = 5;
                        for (final TeamPlayer teamPlayer : team.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer.isAlive() || teamPlayer.isDisconnected()) ? "&7&m" : "") + teamPlayer.getUsername()).slot(added2).skin(new SkinTexture(Skin.getPlayer(teamPlayer.getPlayer()).getProperty().getValue(), Skin.getPlayer(teamPlayer.getPlayer()).getProperty().getSignature())));
                            if (++added2 >= 20) {
                                break;
                            }
                        }
                        elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lMatch Info").slot(23));
                        elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration: " + tabManager.getMainColor() + "" + match2.getDuration()).slot(24));
                        elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Arena: " + tabManager.getMainColor() + "" + match2.getArena().getName()).slot(25));
                        int added3 = 45;
                        elements.add(new BufferedTabObject().text("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")").slot(44));
                        for (final TeamPlayer teamPlayer2 : opponentTeam.getTeamPlayers()) {
                            elements.add(new BufferedTabObject().text(" " + ((!teamPlayer2.isAlive() || teamPlayer2.isDisconnected()) ? "&7&m" : "") + teamPlayer2.getUsername()).slot(added3).skin(new SkinTexture(Skin.getPlayer(teamPlayer2.getPlayer()).getProperty().getValue(), Skin.getPlayer(teamPlayer2.getPlayer()).getProperty().getSignature())));
                            if (++added3 >= 60) {
                                break;
                            }
                        }
                    }
                }
                else if (match2.isFreeForAllMatch()) {
                    final Team team = match2.getTeam(player);
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lMatch Info").slot(23));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Opponents: " + tabManager.getMainColor() + team.getAliveCount() + "/" + team.getTeamPlayers().size()).slot(25));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration: " + tabManager.getMainColor() + match2.getDuration()).slot(27));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Arena: " + tabManager.getMainColor() + match2.getArena().getDisplayName()).slot(28));
                }
            }
        }
        else if (profile.isInBrackets()) {
            final Brackets brackets = profile.getBrackets();
            elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lBrackets").slot(23));
            int pl = 0;
            for (int added4 = 0; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (brackets.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player bracketsPlayer = brackets.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + bracketsPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInGulag()) {
            final Gulag gulag = profile.getGulag();
            elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lGulag").slot(23));
            int pl = 0;
            for (int added4 = 0; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (gulag.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player gulagPlayer = gulag.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + gulagPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInSumo()) {
            final Sumo sumo = profile.getSumo();
            elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lSumo").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (sumo.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player sumoPlayer = sumo.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + sumoPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInLMS()) {
            final LMS ffa = profile.getLms();
            elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lLMS").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (ffa.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player ffaPlayer = ffa.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + ffaPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInParkour()) {
            final Parkour parkour = profile.getParkour();
            elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lParkour").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (parkour.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player parkourPlayer = parkour.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + parkourPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isInSpleef()) {
            final Spleef spleef = profile.getSpleef();
            elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lSpleef").slot(23));
            int pl = 0;
            for (int added4 = 4; added4 < 60; ++added4) {
                if (!tabSlots.contains(added4)) {
                    if (spleef.getRemainingPlayers().size() <= pl) {
                        break;
                    }
                    final Player spleefPlayer = spleef.getRemainingPlayers().get(pl).getPlayer();
                    ++pl;
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + spleefPlayer.getName()).slot(added4));
                }
            }
        }
        else if (profile.isSpectating()) {
            final Match match2=profile.getMatch();
            if (match2 != null) {
                if (match2.isSoloMatch() || match2.isTheBridgeMatch()) {
                    elements.add(new BufferedTabObject().text("&aPlayer A").slot(4));
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lMatch Info").slot(23));
                    elements.add(new BufferedTabObject().text("&aPlayer B").slot(44));
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + match2.getTeamPlayerA().getUsername()).slot(5));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration:").slot(24));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + match2.getDuration()).slot(25));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + match2.getTeamPlayerB().getUsername()).slot(45));
                } else if (match2.isTeamMatch() || match2.isHCFMatch()) {
                    elements.add(new BufferedTabObject().text("&aTeam A").slot(4));
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lMatch Info").slot(23));
                    elements.add(new BufferedTabObject().text("&aTeam B").slot(44));
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + match2.getTeamA().getLeader().getUsername()).slot(5));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration:").slot(24));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + match2.getDuration()).slot(25));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + match2.getTeamB().getLeader().getUsername()).slot(45));
                } else if (match2.isFreeForAllMatch()) {
                    final int team=match2.getPlayers().size();
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lFFA Match Info").slot(23));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Players: " + tabManager.getMainColor() + team).slot(25));
                    elements.add(new BufferedTabObject().text(tabManager.getSecondaryColor() + "Duration:").slot(27));
                    elements.add(new BufferedTabObject().text(tabManager.getMainColor() + match2.getDuration()).slot(28));
                }
            }
            final Spleef spleef = profile.getSpleef();
            if(spleef != null) {
                elements.add(new BufferedTabObject().text(tabManager.getMainColor() + "&lSpectating Spleef").slot(23));
                int pl=0;
                for ( int added4=4; added4 < 60; ++added4 ) {
                    if (!tabSlots.contains(added4)) {
                        if (spleef.getRemainingPlayers().size() <= pl) {
                            break;
                        }
                        final Player spleefPlayer=spleef.getRemainingPlayers().get(pl).getPlayer();
                        ++pl;
                        elements.add(new BufferedTabObject().text(tabManager.getMainColor() + spleefPlayer.getName()).slot(added4));
                    }
                }
            }
        }
    } else {
        int pl=0;
        for ( int added4=1; added4 < 60; ++added4 ) {
            elements.add(new BufferedTabObject().slot(21).text(tabManager.getMainColor() + tabManager.getLegacyHeader()));
            elements.add(new BufferedTabObject().slot(20).text(tabManager.getSecondaryColor() + essentials.socialMeta.getTwitter()).skin(new SkinTexture(Skin.TWITTER_SKIN.getProperty().getValue(), Skin.TWITTER_SKIN.getProperty().getSignature())));
            elements.add(new BufferedTabObject().slot(60).text(tabManager.getSecondaryColor() + essentials.socialMeta.getDiscord()).skin(new SkinTexture(Skin.DISCORD_SKIN.getProperty().getValue(), Skin.DISCORD_SKIN.getProperty().getSignature())));
            elements.add(new BufferedTabObject().slot(40).text(tabManager.getSecondaryColor() + essentials.socialMeta.getWebiste()).skin(new SkinTexture(Skin.WEBSITE_SKIN.getProperty().getValue(), Skin.WEBSITE_SKIN.getProperty().getSignature())));

            tabSlots.add(0);
            tabSlots.add(1);
            tabSlots.add(21);
            tabSlots.add(23);
            tabSlots.add(41);
            tabSlots.add(43);
            elements.add(new BufferedTabObject().slot(67).text(tabManager.getMainColor() + "&lIMPORTANT NOTICE"));
            elements.add(new BufferedTabObject().slot(68).text(tabManager.getSecondaryColor() + "Please use"));
            elements.add(new BufferedTabObject().slot(69).text(tabManager.getSecondaryColor() + "version " + tabManager.getMainColor() + "&l1.7"));
            elements.add(new BufferedTabObject().slot(70).text(tabManager.getSecondaryColor() + "for optimal"));
            elements.add(new BufferedTabObject().slot(71).text(tabManager.getSecondaryColor() + "gameplay experience"));
            if (!tabSlots.contains(added4)) {
                if (Profile.getPlayerList().size() <= pl) {
                    break;
                }
                Player player1 = Profile.getPlayerList().get(pl).getPlayer();
                ++pl;
                elements.add(new BufferedTabObject().skin(new SkinTexture(Skin.getPlayer(player1).getProperty().getValue(), Skin.getPlayer(player1).getProperty().getSignature())).text(Array.getInstance().getRankManager().getRankColor(player1) + player1.getName()).slot(added4).ping(player1.spigot().getPing()));
            }
        }
    }
        return elements;
    }
}