package me.drizzy.practice.tablist;

import com.mojang.authlib.properties.Property;
import me.drizzy.practice.Array;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.tab.ZigguratAdapter;
import me.drizzy.practice.util.tab.utils.BufferedTabObject;
import me.drizzy.practice.util.tab.utils.SkinTexture;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
        final int[] ipSlots = { 2, 22, 42};
        elements.add(new BufferedTabObject().slot(21).text("&b&lPractice &7| &f&lEU"));
        for (final int ipSlot : ipSlots) {
            elements.add(new BufferedTabObject().slot(19).text("&b&lStore").skin(new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTYxMDE2ODAwNTY0MywKICAicHJvZmlsZUlkIiA6ICJkZTE0MGFmM2NmMjM0ZmM0OTJiZTE3M2Y2NjA3MzViYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTUlRlYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhjYzJhZDNmYWEwMmYzMzUwMjc3YjYwM2U0ZWI2MTg1ZWFiZDQ3NDM5ZDJkZmQwZjc2MjRlNjg2MDUzZjZhYSIKICAgIH0KICB9Cn0", "oyyOVbMeukjSzkHAh6SgrLVQjR3WqVv68gFgjIAlrRfzCnqpmqepWpUQBkUVWrO3TjC4YeevQACSCr9gwxIqqZoLnlunklo4e4DekCKE+SN710ljf/bCfHnEUU1Ey4B5ZcqcIKBdjadftftDzuCdswae1o1VADBO6JFxREUqm3tCZooSf9yUf+YdUc3+W/OWN8OGpOwHLOXBy2EpIuXrH//YhZ2Ve4Lbic22kBDXljvKj3U799XjVyUngaMHE5phPha20eg/KRtx/laXwziX9Uk053I7owIqjRxtpmUG8FIYcKALJ491pdjURmUbv21KD0DZOcP22JYwu62OAuvA/xI95sB+5HtfJYEOOa8+O/RMv2fLcsiXkxlfiWlpbyHxLRbsuGtT2zJ1vARoEK/pwU9tGTpIefElEzi5hWeDyzshBVYTeeTYEopO9ApiytHbEgU1bBGWFVNUv/u1RkxH3qjmMast9MUf9Tr20QubZNLkXswZD3WASMABPV8RlLhME9nqObfGWmnlO4Z6nFT3E8FvojAxcA2XEHiHMAmd+PqO1a/h1E2Nv8C3xUeXHY8Zk4AlQghHV+HkUJCgJMAM2r0c07Opgkm6HdKsuJ83svr90c87pTU4uPLI8gUasWMihhA5bWPCtqBJwW6C7kEjX+cOAmsT8UmIPcgsnItWRKo=")));
            elements.add(new BufferedTabObject().slot(20).text("&fstore.purge.com"));
            elements.add(new BufferedTabObject().slot(59).text("&b&lDiscord").skin(new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTYxMDE2ODMyNjA3NSwKICAicHJvZmlsZUlkIiA6ICI2MWVhMDkyM2FhNDQ0OTEwYmNlZjViZmQ2ZDNjMGQ1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVEYXJ0aEZhdGhlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YzUzN2I0MjMwYjFmMjE2MDU0NTUyODgzYjU1MDQwODg3MDAwNDNmZTgzYmM5ZDY0MjlhMTIyMjUzZjQ3ZjJiIgogICAgfQogIH0KfQ==", "pf/G7DG9uWfLp2msnqOH4R5YX8k/oHqGGKS0PtspdQRkfsMPkp5XiZW+HJNF3P0go0Cmoq33Q2ogVxfXH/8W2gXP+5qcNDd4zlNUsKMLTzoHXEB/ZwX7ESyz4qEzq+CRCdjoavRfyvZL+g94SqU8TmYuHSUBzOEJeaIwFiVZAaw7fTVj5dprknpvMHri9CaawgxszAOz0LsWPxgbZVLiprBPyjbDiIXOR+yFB8pdnLYX3p2WsQ4vRAbNCNrOutS3pOyqMnIbfaLjt+hwHK79cALh/TdPi9nPujLj/GIW7ZW3kqeSBd11j8hfZ2W3Vfmr8h4yEbJed1tKFkiCJ/4eT6C/MKGEnQRQ3mDjRuDzuY+LjroEa32jS8YEbguUoQxrphP4LDOB4x4yMaL4HWVpCT5yLlV2ORfgCk+UKhmsciZCr5Dx7gpjdCsVUErMxGgnxJuGeZCKt46SeD8aUcB+NooueRGHUCjPEVT+gX1bw2Ndw52Hjbxm6PgOabc9dYPGSUejIyDaZx4MgVjOqvJOB++nhDTT8zbpkyNpTY6no24TXcOkEpL9PMpxjQcg+WawdegrT+IJvpaB16SaBkltgnLhL+FyO6+/sKLgvC8Y+oI7lgTHhlrt0eUfHMoIKP4cv3ELVsiCOVTdl9LNw6IWPlxzlb7ZFxczuwMwuIXqlpo=")));
            elements.add(new BufferedTabObject().slot(60).text("&fdiscord.purge.com"));
            elements.add(new BufferedTabObject().slot(39).text("&b&lWebsite").skin(new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTYxMTgxMTQ4MTc2NCwKICAicHJvZmlsZUlkIiA6ICI0OWIzODUyNDdhMWY0NTM3YjBmN2MwZTFmMTVjMTc2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJiY2QyMDMzYzYzZWM0YmY4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc4NjdjMGZhZGY0YzVkMDk5MDcxNzYxYmVkNjlmYjQyMTMwMDE2YmU3Njg1NmMxMjNjZjM4Njk1MTY1NDk0NzQiCiAgICB9CiAgfQp9", "YVnjGNGIoO97WJFehSbnGZP16yMDbC8hi311MxZC9XzIR5qruq8inQLdON8RPKUDzcFg848LkqwobDzDPTg5wLeLOQc1AD2BCA+Th/Z5nTjgcOn19MYbgscLZriXsTQMhMI6TcVzaGMTa8KgPaSFz8UZB3zzkTGRZC/YofAiJrlSKi94Xj49Ln1OLNsnD+BZRWG6JHOlEkryMkBVf8oeTS4iY9Cp2XUeSGm83XhBkmSFixaNJ2rD2CjAk8cWcZgYb4rqbevUai8haUMwli/ARyGNnFnOHV8lv1ruheE5MwWOdQHUJZmYX6V+MYv31YCpsgABs6YWQqeU4BO/5f947VJVoNn93v5oM6oi/a7j9ChDYKeOzwpmL+wktnO9Ka8gPPbrIr1K9X3KTWvvNAXv/vufhbTZaEeDyzZ4asmz6kZOl7FCXiB/QBnQ89RJ/e9vfHsLyXutgnOLfsQNTwJyy7cvi7YjtVhIsIiSssduW+HtleMdXkdo8K1lMiisRmceK45c16Jh8Lhhxp1nfSBEyZV4zVTc+5Q+O0Hg8o7XURx6u7kqWY7XzmCjB91JUbr7lEkBRPcoIW7RjyVVQSLFTTnCfJcHr6hRNR4nuYlNXptCfjp8Z2MIO8Db5preUR42YQlgEuM8opIOSBfgHvfQom9a7ehZKtfvkfLnVWUlWIA=")));
            elements.add(new BufferedTabObject().slot(40).text("&fwww.purge.com"));
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
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + kit.getName() + ": " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + profile.getStatisticsData().get(kit).getElo()).slot(statslots));
                        if (++statslots >= 20) {
                            break;
                        }
                    }
                }
                elements.add(new BufferedTabObject().slot(23).text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Lobby Info"));
                elements.add(new BufferedTabObject().slot(24).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + " Online: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ArrayCache.getOnline()));
                elements.add(new BufferedTabObject().slot(25).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + " In Queue: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ArrayCache.getInQueues()));
                elements.add(new BufferedTabObject().slot(26).text(Array.getInstance().getMainConfig().getString("Tab.Color.Elements") + " In Fight: " + Array.getInstance().getMainConfig().getString("Tab.Color.Main") + ArrayCache.getInFights()));
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
                    if (match2.isTheBridgeMatch()) {
                        elements.add(new BufferedTabObject().text("&b&lPoints").slot(27));
                        Profile profile1 = Profile.getByUuid(match2.getOpponentPlayer(player));
                        elements.add(new BufferedTabObject().text("&aYour Points &8- &f" + profile.getBridgeRounds()).slot(8));
                        elements.add(new BufferedTabObject().text("&cTheir Pioints &8- &f" + profile1.getBridgeRounds()).slot(48));
                    }
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
                        elements.add(new BufferedTabObject().text("&cOpponents &8- &f" + opponentTeam.getSumoRounds()).slot(48));

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
                if (match2.isSoloMatch() || match2.isTheBridgeMatch()) {
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
            final Spleef spleef = profile.getSpleef();
            if(spleef != null) {
                elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + CC.BOLD + "Spectating Spleef").slot(23));
                int pl=0;
                for ( int added4=4; added4 < 60; ++added4 ) {
                    if (!tabSlots.contains(added4)) {
                        if (spleef.getRemainingPlayers().size() <= pl) {
                            break;
                        }
                        final Player spleefPlayer=spleef.getRemainingPlayers().get(pl).getPlayer();
                        ++pl;
                        elements.add(new BufferedTabObject().text(Array.getInstance().getMainConfig().getString("Tab.Color.Main") + spleefPlayer.getName()).slot(added4));
                    }
                }
            }
        }
    } else {
        int pl=0;
        for ( int added4=1; added4 < 60; ++added4 ) {
            elements.add(new BufferedTabObject().text("&b&lPractice &7| &f&lEU").slot(21));
            elements.add(new BufferedTabObject().slot(20).text("&7store.purge.com").skin(new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTYxMDE2ODAwNTY0MywKICAicHJvZmlsZUlkIiA6ICJkZTE0MGFmM2NmMjM0ZmM0OTJiZTE3M2Y2NjA3MzViYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTUlRlYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhjYzJhZDNmYWEwMmYzMzUwMjc3YjYwM2U0ZWI2MTg1ZWFiZDQ3NDM5ZDJkZmQwZjc2MjRlNjg2MDUzZjZhYSIKICAgIH0KICB9Cn0", "oyyOVbMeukjSzkHAh6SgrLVQjR3WqVv68gFgjIAlrRfzCnqpmqepWpUQBkUVWrO3TjC4YeevQACSCr9gwxIqqZoLnlunklo4e4DekCKE+SN710ljf/bCfHnEUU1Ey4B5ZcqcIKBdjadftftDzuCdswae1o1VADBO6JFxREUqm3tCZooSf9yUf+YdUc3+W/OWN8OGpOwHLOXBy2EpIuXrH//YhZ2Ve4Lbic22kBDXljvKj3U799XjVyUngaMHE5phPha20eg/KRtx/laXwziX9Uk053I7owIqjRxtpmUG8FIYcKALJ491pdjURmUbv21KD0DZOcP22JYwu62OAuvA/xI95sB+5HtfJYEOOa8+O/RMv2fLcsiXkxlfiWlpbyHxLRbsuGtT2zJ1vARoEK/pwU9tGTpIefElEzi5hWeDyzshBVYTeeTYEopO9ApiytHbEgU1bBGWFVNUv/u1RkxH3qjmMast9MUf9Tr20QubZNLkXswZD3WASMABPV8RlLhME9nqObfGWmnlO4Z6nFT3E8FvojAxcA2XEHiHMAmd+PqO1a/h1E2Nv8C3xUeXHY8Zk4AlQghHV+HkUJCgJMAM2r0c07Opgkm6HdKsuJ83svr90c87pTU4uPLI8gUasWMihhA5bWPCtqBJwW6C7kEjX+cOAmsT8UmIPcgsnItWRKo=")));
            elements.add(new BufferedTabObject().slot(60).text("&7discord.purge.com").skin(new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTYxMDE2ODMyNjA3NSwKICAicHJvZmlsZUlkIiA6ICI2MWVhMDkyM2FhNDQ0OTEwYmNlZjViZmQ2ZDNjMGQ1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVEYXJ0aEZhdGhlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84YzUzN2I0MjMwYjFmMjE2MDU0NTUyODgzYjU1MDQwODg3MDAwNDNmZTgzYmM5ZDY0MjlhMTIyMjUzZjQ3ZjJiIgogICAgfQogIH0KfQ==", "pf/G7DG9uWfLp2msnqOH4R5YX8k/oHqGGKS0PtspdQRkfsMPkp5XiZW+HJNF3P0go0Cmoq33Q2ogVxfXH/8W2gXP+5qcNDd4zlNUsKMLTzoHXEB/ZwX7ESyz4qEzq+CRCdjoavRfyvZL+g94SqU8TmYuHSUBzOEJeaIwFiVZAaw7fTVj5dprknpvMHri9CaawgxszAOz0LsWPxgbZVLiprBPyjbDiIXOR+yFB8pdnLYX3p2WsQ4vRAbNCNrOutS3pOyqMnIbfaLjt+hwHK79cALh/TdPi9nPujLj/GIW7ZW3kqeSBd11j8hfZ2W3Vfmr8h4yEbJed1tKFkiCJ/4eT6C/MKGEnQRQ3mDjRuDzuY+LjroEa32jS8YEbguUoQxrphP4LDOB4x4yMaL4HWVpCT5yLlV2ORfgCk+UKhmsciZCr5Dx7gpjdCsVUErMxGgnxJuGeZCKt46SeD8aUcB+NooueRGHUCjPEVT+gX1bw2Ndw52Hjbxm6PgOabc9dYPGSUejIyDaZx4MgVjOqvJOB++nhDTT8zbpkyNpTY6no24TXcOkEpL9PMpxjQcg+WawdegrT+IJvpaB16SaBkltgnLhL+FyO6+/sKLgvC8Y+oI7lgTHhlrt0eUfHMoIKP4cv3ELVsiCOVTdl9LNw6IWPlxzlb7ZFxczuwMwuIXqlpo=")));
            elements.add(new BufferedTabObject().slot(40).text("&fwww.purge.com").skin(new SkinTexture("ewogICJ0aW1lc3RhbXAiIDogMTYxMTgxMTQ4MTc2NCwKICAicHJvZmlsZUlkIiA6ICI0OWIzODUyNDdhMWY0NTM3YjBmN2MwZTFmMTVjMTc2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJiY2QyMDMzYzYzZWM0YmY4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc4NjdjMGZhZGY0YzVkMDk5MDcxNzYxYmVkNjlmYjQyMTMwMDE2YmU3Njg1NmMxMjNjZjM4Njk1MTY1NDk0NzQiCiAgICB9CiAgfQp9", "YVnjGNGIoO97WJFehSbnGZP16yMDbC8hi311MxZC9XzIR5qruq8inQLdON8RPKUDzcFg848LkqwobDzDPTg5wLeLOQc1AD2BCA+Th/Z5nTjgcOn19MYbgscLZriXsTQMhMI6TcVzaGMTa8KgPaSFz8UZB3zzkTGRZC/YofAiJrlSKi94Xj49Ln1OLNsnD+BZRWG6JHOlEkryMkBVf8oeTS4iY9Cp2XUeSGm83XhBkmSFixaNJ2rD2CjAk8cWcZgYb4rqbevUai8haUMwli/ARyGNnFnOHV8lv1ruheE5MwWOdQHUJZmYX6V+MYv31YCpsgABs6YWQqeU4BO/5f947VJVoNn93v5oM6oi/a7j9ChDYKeOzwpmL+wktnO9Ka8gPPbrIr1K9X3KTWvvNAXv/vufhbTZaEeDyzZ4asmz6kZOl7FCXiB/QBnQ89RJ/e9vfHsLyXutgnOLfsQNTwJyy7cvi7YjtVhIsIiSssduW+HtleMdXkdo8K1lMiisRmceK45c16Jh8Lhhxp1nfSBEyZV4zVTc+5Q+O0Hg8o7XURx6u7kqWY7XzmCjB91JUbr7lEkBRPcoIW7RjyVVQSLFTTnCfJcHr6hRNR4nuYlNXptCfjp8Z2MIO8Db5preUR42YQlgEuM8opIOSBfgHvfQom9a7ehZKtfvkfLnVWUlWIA=")));

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
                elements.add(new BufferedTabObject().skin(this.getHeadByPlayer(player1)).text(Array.getInstance().getRankManager().getRankColor(player1) + player1.getName()).slot(added4).ping(player1.spigot().getPing()));
            }
        }
    }
        return elements;
    }


      public SkinTexture getHeadByPlayer(Player player) {
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            Property property = entityPlayer.getProfile().getProperties().get("textures").iterator().next();
           return new SkinTexture(property.getValue(), property.getSignature());
      }
}