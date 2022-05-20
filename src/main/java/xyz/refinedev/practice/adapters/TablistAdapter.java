/*package xyz.refinedev.practice.adapters;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.FFAMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.pvpclasses.PvPClass;
import xyz.refinedev.practice.pvpclasses.classes.Bard;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.tablist.adapter.TabAdapter;
import xyz.refinedev.tablist.construct.TabEntry;
import xyz.refinedev.tablist.util.Skin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Skin;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

@RequiredArgsConstructor
public class TablistAdapter implements TabAdapter {

    private final Array plugin;
    private final BasicConfigurationFile config;

    @Override
    public String getHeader(Player player) {
        return String.join("\n", config.getStringList("TABLIST.HEADER"));
    }

    @Override
    public String getFooter(Player player) {
        return String.join( "\n", config.getStringList("TABLIST.FOOTER"));
    }

    @Override
    public List<TabEntry> getLines(Player player) {
        List<TabEntry> entries = new ArrayList<>();
        Profile profile = plugin.getProfileManager().getProfile(player);

        if (profile.getSettings().isVanillaTab()) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            players.sort(new TabComparator());

            for ( int i = 0; i < 80; i++ ) {
                final int x = i % 4;
                final int y = i / 4;

                if (players.size() <= i) continue;

                Player tabPlayer = players.get(i);
                if (tabPlayer == null) continue;

                entries.add(new TabEntry(x, y, tabPlayer.getDisplayName(), tabPlayer.spigot().getPing(), Skin.getPlayer(tabPlayer)));
            }

            return entries;
        }
        
        if ((profile.isInLobby() || profile.isInQueue()) && profile.getParty() == null) {
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.LEFT." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.MIDDLE." + (i + 1));

                if (string == null) continue;
                

                TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.RIGHT." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("LOBBY.FAR-RIGHT." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceLobby(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
        } else if ((profile.isInLobby() || profile.isInQueue()) && profile.getParty() != null) {
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("IN-PARTY.LEFT." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceParty(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("IN-PARTY.MIDDLE." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceParty(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("IN-PARTY.RIGHT." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceParty(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
            for ( int i = 0; i < 20; i++ ) {
                String string = config.getString("IN-PARTY.FAR-RIGHT." + (i + 1));

                if (string == null) continue;

                TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceParty(player, string)));
                TabEntry entry = checkSkin(player, rawEntry);
                TabEntry finalEntry = checkDot(entry);

                entries.add(finalEntry);
            }
        } else if (profile.isInMatch() && !profile.isSpectating()) {
            if (profile.getMatch().isSoloMatch()) {
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.SOLO.LEFT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceSoloMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.SOLO.MIDDLE." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceSoloMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.SOLO.RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceSoloMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.SOLO.FAR-RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceSoloMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
            } else if (profile.getMatch().isTeamMatch()) {
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.TEAM.LEFT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceTeamMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.TEAM.MIDDLE." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceTeamMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.TEAM.RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceTeamMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.TEAM.FAR-RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceTeamMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
            } else if (profile.getMatch().isFreeForAllMatch()) {
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.FFA.LEFT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceFFAMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.FFA.MIDDLE." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceFFAMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.FFA.RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceFFAMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.FFA.FAR-RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceFFAMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
            } else if (profile.getMatch().isHCFMatch()) {
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.HCF.LEFT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(0, i, CC.translate(replaceHCFMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.HCF.MIDDLE." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(1, i, CC.translate(replaceHCFMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.HCF.RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(2, i, CC.translate(replaceHCFMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
                for ( int i = 0; i < 20; i++ ) {
                    String string = config.getString("MATCH.HCF.FAR-RIGHT." + (i + 1));

                    if (string == null) continue;

                    TabEntry rawEntry = new TabEntry(3, i, CC.translate(replaceHCFMatch(player, string)));
                    TabEntry entry = checkSkin(player, rawEntry);
                    TabEntry finalEntry = checkDot(entry);

                    entries.add(finalEntry);
                }
            }
        }
        return entries;
    }
    
    public String replaceLobby(Player player, String toReplace) {
        return replaceStats(player, toReplace)
                .replace("<in_fight>", String.valueOf(plugin.getMatchManager().getInFights()))
                .replace("<in_queue>", String.valueOf(plugin.getQueueManager().getInQueues()))
                .replace("<online_count>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()));
    }

    public String replaceStats(Player player, String toReplace) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        String eloFormat = config.getString("TABLIST.ELO_FORMAT");

        if (toReplace == null) return "";

        for ( Kit kit : plugin.getKitManager().getKits()) {
            String kitName = kit.getName();
            int elo = profile.getStatisticsData().get(kit).getElo();

            if (toReplace.contains("<kit_" + kitName + ">")) {
                toReplace = toReplace.replace("<kit_" + kitName + ">", eloFormat.replace("<name>", kitName).replace("<elo>", String.valueOf(elo)));
            }
        }

        if (plugin.isPlaceholderAPI()) {
            toReplace = PlaceholderAPI.setPlaceholders(player, toReplace);
        }
        return toReplace
                .replace("<profile_global_elo>", String.valueOf(profile.getGlobalElo())
                .replace("<profile_wins>", String.valueOf(profile.getTotalWins()))
                .replace("<profile_losses>", String.valueOf(profile.getTotalLost()))
                .replace("<profile_name>", profile.getName()))
                .replace("<profile_wlr>", plugin.getProfileManager().getWLR(profile))
                .replace("<profile_division>", plugin.getProfileManager().getDivision(profile).getDisplayName());
    }

    public String replaceParty(Player player, String toReplace) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        String armorClass = party.getKits().get(player.getUniqueId());
        List<TeamPlayer> teamPlayers = party.getTeamPlayers();

        for ( int i = 0; i < 25; i++ ) {
            String member = "<party_member_" + ((i + 1)) + ">";
            if (teamPlayers.size() <= i) {
                toReplace = toReplace.replace(member, "&7");
            } else {
                String name = teamPlayers.get(i).getUsername();
                if (toReplace.contains(member)) {
                    toReplace = toReplace.replace(member, name == null ? CC.translate("&7") : name);
                }
            }
        }
        
        return replaceLobby(player, toReplace
                .replace("<party_leader>", party.getLeader().getUsername())
                .replace("<party_size>", String.valueOf(party.getPlayers().size()))
                .replace("<party_privacy>", party.getPrivacy())
                .replace("<party_limit>", String.valueOf(party.getLimit())))
                .replace("<party_class>", armorClass == null ? "None" : armorClass);
    }

    public String replaceSoloMatch(Player player, String toReplace) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Match match = profile.getMatch();

        int playerAPing = PlayerUtil.getPing(match.getTeamPlayerA().getPlayer());
        int playerBPing = PlayerUtil.getPing(match.getTeamPlayerB().getPlayer());

        return replaceTeamPlayer(player, replaceLobby(player, toReplace))
                .replace("<match_kit>", match.getKit().getDisplayName())
                .replace("<match_arena>", match.getArena().getDisplayName())
                .replace("<match_duration>", match.getDuration())
                .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                .replace("<playerA_ping>", String.valueOf(playerAPing))
                .replace("<playerB_ping>", String.valueOf(playerBPing))
                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                .replace("<match_type>", "Solo");
    }

    public String replaceTeamMatch(Player player, String toReplace) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Match match = profile.getMatch();

        Team team = match.getTeam(player);
        Team opponent = match.getOpponentTeam(player);

        //Your Team Replacement
        for ( int i = 0; i < 30; i++ ) {
            String member = "<team_" + ((i + 1)) + ">";
            toReplace = getFormattedName(player, toReplace, match, team, i, member);
        }

        //Opponent Team Replacement
        for ( int i = 0; i < 30; i++ ) {
            String member = "<opponent_" + ((i + 1)) + ">";
            toReplace = getFormattedName(player, toReplace, match, opponent, i, member);
        }

        return replaceTeamPlayer(player, replaceLobby(player, toReplace))
                .replace("<match_duration>", match.getDuration())
                .replace("<match_kit>", match.getKit().getDisplayName())
                .replace("<match_type>", "Team")
                .replace("<match_arena>", match.getArena().getDisplayName())
                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                .replace("<your_name>", player.getName())
                .replace("<your_team_alive>", String.valueOf(team.getAliveCount()))
                .replace("<opponent_team_alive>", String.valueOf(opponent.getAliveCount()))
                .replace("<your_team_count>", String.valueOf(team.getPlayers().size()))
                .replace("<opponent_team_count>", String.valueOf(opponent.getPlayers().size())).replace("%splitter%", "┃").replace("|", "┃");
    }

    public String replaceTeamPlayer(Player player, String toReplace) {
        Profile profile = plugin.getProfileManager().getProfile(player);

        Match match = profile.getMatch();
        if (match == null) return toReplace;

        TeamPlayer teamPlayer = match.getTeamPlayer(player);
        if (teamPlayer == null) return toReplace;

        return toReplace
                .replace("<profile_combos>", String.valueOf(teamPlayer.getCombo()))
                .replace("<profile_elo>", String.valueOf(teamPlayer.getElo()))
                .replace("<profile_cps>", String.valueOf(teamPlayer.getCps()))
                .replace("<profile_hits>", String.valueOf(teamPlayer.getHits()))
                .replace("<profile_ping>", String.valueOf(teamPlayer.getPing()))
                .replace("<profile_pot_accuracy>", String.valueOf(teamPlayer.getPotionAccuracy()));
    }

    public String replaceHCFMatch(Player player, String toReplace) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        Match match = profile.getMatch();
        PvPClass pvpClass = plugin.getPvpClassManager().getEquippedClass(player);

        Team team = match.getTeam(player);
        Team opponentTeam = match.getOpponentTeam(player);

        //Your Team Replacement
        for ( int i = 0; i < 30; i++ ) {
            String member = "<team_" + ((i + 1)) + ">";
            toReplace = getArmorFormattedName(toReplace, party, team, opponentTeam, i, member);
        }

        //Opponent Team Replacement
        for ( int i = 0; i < 30; i++ ) {
            String member = "<opponent_" + ((i + 1)) + ">";
            toReplace = getArmorFormattedName(toReplace, party, opponentTeam, opponentTeam, i, member);
        }

        toReplace = toReplace.replace("<bard_energy>", pvpClass instanceof Bard ? String.valueOf(((Bard)pvpClass).getEnergy(player)) : "None");

        return toReplace
                .replace("<match_duration>", match.getDuration())
                .replace("<match_kit>", "HCF")
                .replace("<match_arena>", match.getArena().getDisplayName())
                .replace("<match_type>", "HCF")
                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                .replace("<your_name>", player.getName())
                .replace("<your_class>", pvpClass == null ? "None" : pvpClass.getName())
                .replace("<your_team_alive>", String.valueOf(team.getAliveCount()))
                .replace("<opponent_team_alive>", String.valueOf(opponentTeam.getAliveCount()))
                .replace("<your_team_count>", String.valueOf(team.getPlayers().size()))
                .replace("<opponent_team_count>", String.valueOf(opponentTeam.getPlayers().size()))
                .replace("%splitter%", "┃")
                .replace("|", "┃");
    }


    public String replaceFFAMatch(Player player, String toReplace) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        FFAMatch match = (FFAMatch) profile.getMatch();
        Team team = match.getTeam(player);
        List<TeamPlayer> fixedPlayers = team.getTeamPlayers().stream().filter(teamPlayer -> !teamPlayer.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());

        String yourPlayer = profile.getName();

        for ( int i = 0; i < 30; i++ ) {
            String member = "<opponent_" + ((i + 1)) + ">";
            toReplace = this.getFFAFormattedName(player, toReplace, match, fixedPlayers, i, member);
        }

        return replaceTeamPlayer(player, replaceLobby(player, toReplace))
                .replace("<your_player>", yourPlayer)
                .replace("<match_duration>", match.getDuration())
                .replace("<match_kit>", match.getKit().getDisplayName())
                .replace("<match_arena>", match.getArena().getDisplayName())
                .replace("<match_type>", "FFA")
                .replace("<total_count>", String.valueOf(team.getPlayers().size()))
                .replace("<alive_count>", String.valueOf(team.getAliveCount()))
                .replace("%splitter%", "┃").replace("|", "┃");
    }

    //TODO: Complete Event Tablist
    public void replaceEvent(Player player, String toReplace) {

    }

    private String getFormattedName(Player player, String toReplace, Match match, Team opponent, int i, String member) {
        if (opponent.getPlayers().size() <= i) return toReplace.replace(member, "&7");

        TeamPlayer teamPlayer = opponent.getTeamPlayers().get(i);
        String name = teamPlayer.getUsername();

        if (toReplace.contains(member)) {
            toReplace = toReplace
                    .replace(member, name == null ?
                            CC.translate("&7") : !teamPlayer.isAlive() ?
                            CC.GRAY + CC.STRIKE_THROUGH + name :  match.getRelationColor(player, teamPlayer.getPlayer()) + name);
        }
        return toReplace;
    }

    private String getArmorFormattedName(String toReplace, Party party, Team team, Team opponentTeam, int i, String member) {
        if (team.getPlayers().size() <= i) return toReplace.replace(member, "&7");

        TeamPlayer teamPlayer = opponentTeam.getTeamPlayers().get(i);
        String name = teamPlayer.getUsername();

        String pvPClass = party.getKits().get(teamPlayer.getUniqueId());
        String displayName = pvPClass == null ? name : getClassColor(pvPClass) + name;

        if (toReplace.contains(member)) {
            toReplace = toReplace.replace(member, name == null ? CC.translate("&7") : !teamPlayer.isAlive() ? CC.GRAY + CC.STRIKE_THROUGH + name : displayName);
        }

        return toReplace;
    }

    private String getFFAFormattedName(Player player, String toReplace, Match match, List<TeamPlayer> fixedPlayers, int i, String member) {
        if (fixedPlayers.size() <= i) return toReplace.replace(member, "&7");

        TeamPlayer teamPlayer = fixedPlayers.get(i);
        String name = teamPlayer.getUsername();

        if (toReplace.contains(member)) {
            toReplace = toReplace.replace(member, name == null ?
                    CC.translate("&7") : !teamPlayer.isAlive() ?
                    CC.GRAY + CC.STRIKE_THROUGH + name : match.getRelationColor(player, teamPlayer.getPlayer()) + name);
        }
        return toReplace;
    }
    
    public TabEntry checkSkin(Player player, TabEntry entry) {
        String text = entry.getText();

        if (text.contains("<opponent_player>")) {
            Profile profile = plugin.getProfileManager().getProfile(player);
            if (profile.getMatch() != null) {
                entry.setSkin(Skin.getPlayer(profile.getMatch().getOpponentPlayer(player)));
                text = text.replace("<opponent_player>", profile.getMatch().getOpponentPlayer(player).getName());
            }
        }
        if (text.contains("<your_player>")) {
            entry.setSkin(Skin.getPlayer(player));
            text = text.replace("<your_player>", player.getName());
        }
        if (text.contains("<skin_twitter>")) {
            entry.setSkin(Skin.TWITTER_SKIN);
            text = text.replace("<skin_twitter>", "");
        }
        if (text.contains("<skin_website>")) {
            entry.setSkin(Skin.WEBSITE_SKIN);
            text = text.replace("<skin_website>", "");
        }
        if (text.contains("<skin_discord>")) {
            entry.setSkin(Skin.DISCORD_SKIN);
            text = text.replace("<skin_discord>", "");
        }
        if (text.contains("<skin_youtube>")) {
            entry.setSkin(Skin.YOUTUBE_SKIN);
            text = text.replace("<skin_youtube>", "");
        }
        entry.setText(text);
        return entry;
    }

    public ChatColor getClassColor(String pvPClass) {
        switch (pvPClass) {
            case "Bard":
                return ChatColor.YELLOW;
            case "Rogue":
                return ChatColor.RED;
            case "Archer":
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.AQUA;
        }
    }

    public TabEntry checkDot(TabEntry entry) {
        String text = entry.getText();
        for ( ChatColor value : ChatColor.values() ) {
            if (text.contains("<dot_" + value.name().toLowerCase())) {
                entry.setSkin(Skin.getDot(value));
                text = text.replace("<dot_" + value.name().toLowerCase() + ">", "");
            }
        }
        entry.setText(replaceMedia(text));
        return entry;
    }

    public String replaceMedia(String text) {
        return text
                .replace("<website>", plugin.getConfigHandler().getWEBSITE())
                .replace("<twitter>", plugin.getConfigHandler().getTWITTER())
                .replace("<discord>", plugin.getConfigHandler().getDISCORD())
                .replace("<store>", plugin.getConfigHandler().getSTORE());
    }

    private class TabComparator implements Comparator<Player> {

        @Override
        public int compare(Player o1, Player o2) {
            Profile profile1 = plugin.getProfileManager().getProfile(o2);
            Profile profile2 = plugin.getProfileManager().getProfile(o1);
            return profile1.getTabPriority() - profile2.getTabPriority();
        }
    }
}
*/

