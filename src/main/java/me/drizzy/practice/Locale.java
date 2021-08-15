package me.drizzy.practice;

import lombok.Getter;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.config.Replacement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Drizzy
 * Created at 4/11/2021
 */

public enum Locale {

    PARTY_INVITED("PARTY.INVITED", "&8[&c&lParty&8] &7You have been invited to join &c<leader>'s &7party."),
    PARTY_CLICK_TO_JOIN("PARTY.CLICK_TO_JOIN", "&8[&c&lParty&8] &c(Click to accept)"),
    PARTY_INVITE_HOVER("PARTY.INVITE_HOVER", "&aClick to to accept this party invite"),
    PARTY_PLAYER_INVITED("PARTY.PLAYER_INVITED", "&8[&c&lParty&8] &c<invited> &7has been invited to your party."),
    PARTY_PLAYER_JOINED("PARTY.PLAYER_JOINED", "&8[&c&lParty&8] &c<joiner> &7joined your party."),
    PARTY_PLAYER_LEFT("PARTY.PLAYER_LEFT", "&8[&c&lParty&8] &c<leaver> &7has left your party."),
    PARTY_PLAYER_KICKED("PARTY.PLAYER_KICK", "&8[&c&lParty&8] &c<leaver> &7has been kicked from your party."),
    PARTY_CREATED("PARTY.CREATED", "&8[&c&lParty&8] &aYou created a party."),
    PARTY_DISABANDED("PARTY.DISBANDED", "&8[&c&lParty&8] &cYour party has been disbanded."),
    PARTY_PUBLIC("PARTY.PUBLIC", "&8[&c&lParty&8] &c<host> &ais hosting a public party"),
    PARTY_PRIVACY("PARTY.PRIVACY", "&8[&c&lParty&8] &7Your party privacy has been changed to &c<privacy>"),
    PARTY_PROMOTED("PARTY.PROMOTED", "&8[&c&lParty&8] &c<promoted> &ahas been promoted to Leader in your party."),
    PARTY_ALREADYHAVE("PARTY.ALREAD_HAVE", "&8[&c&lParty&8] &7You already have a party!"),
    PARTY_NOTLEADER("PARTY.NOTLEADER", "&8[&c&lParty&8] &7You are not the leader of this party!"),
    PARTY_DONOTHAVE("PARTY.DO_NOT_HAVE", "&8[&c&lParty&8] &7You don't have a party!"),
    PARTY_INFO("PARTY.INFO", Arrays.asList(CC.CHAT_BAR, "&cParty Information", CC.CHAT_BAR, "&8 \u2022 &cLeader: <party_leader_name>", "&8 \u2022 &cPrivacy: <party_privacy>", "&8 \u2022 &cMembers: <party_members_formatted>", CC.CHAT_BAR)),
    PARTY_NOTLOBBY("PARTY.NOT_IN_LOBBY", "&8[&c&lParty&8] &7You are not in lobby, please finish your current task!"),
    PARTY_CHAT_FORMAT("PARTY.CHAT_FORMAT", "&8[&cParty&8] &c<player_displayname>&7: &a<message>"),

    ERROR_NOTACTIVE("ERROR.NOT_ACTIVE", "&7There is no current on-going <event> event!"),
    ERROR_NOTPARTOF("ERROR.NOT_PARTOF", "&7You are not part of any on-going <event> event!"),
    ERROR_NOTSPECTATING("ERROR.NOT_SPECTATING", "&7You are not spectating!"),
    ERROR_NOREMATCH("ERROR.NO_REMATCH", "&7You don't have anyone to rematch with!"),
    ERROR_EXPIREREMATCH("ERROR.REMATCH_EXPIRED", "&7Your rematch time has been expired!"),
    ERROR_PEARLSDISABLED("ERROR.PEARLS_DISABLED", "&cYou can't enderpearl in this arena!"),
    ERROR_REMATCHSENT("ERROR.REMATCH_SENT", "&7You have already sent the rematch request!"),
    ERROR_PLAYERNOTFOUND("ERROR.PLAYER_NOT_FOUND", "&7That player does not exist or is not currently online."),
    ERROR_SETTING_NOPERM("ERROR.SETTINGS_NO_PERM", Arrays.asList("&7You don't have permission to use this setting", "&7&oyou can upgrade your rank at &c&ostore.purgecommunity.com&7&o.")),

    RANKED_DISABLED("RANKED.DISABLED", "&7Ranked has been disabled by an Admin!"),
    RANKED_REQUIRED("RANKED.REQUIRED", Arrays.asList("&7You need to win at least &c10 Unranked Matches &7 to queue Ranked!", "&7&oYou can bypass this limit by upgrading your rank at &c&ostore.purgecommunity.com")),

    HCF_CLASS_ENABLED("HCF.CLASS_ENABLED", "&cClass: &f<class> &aenabled!"),
    HCF_COOLDOWN("HCF.COOLDOWN", "&7You cannot use this for another <duration>!"),
    HCF_ARCHER_RANGE("HCF.ARCHER_RANGE", "&cRange: &f<range>"),
    HCF_ARCHER_MARKED("HCF.ARCHER_MARKED_SHOOTER", "&7You Marked &c<damaged> &7for &c10 seconds &c<damagedhealth> &4❤"),
    HCF_ARCHER_DAMAGEMARKED("HCF.ARCHER_MARKED_DAMAGED", "&7Marked! &c<shooter> &7has shot &cyou &7and &cmarked &7you (+25% damage) for &c10 seconds&7. &8(&c<distance> blocks away&8)"),
    HCF_BARD_ENERGY("HCF.BARD_ENERGY", "&cBard Energy: &f<energy>"),
    HCF_BARD_BARDBUFF("HCF.BARD_BARDBUFF", "&7You have just used a &c&lBard Buff &7that cost you &c<cost> &7of your Energy."),
    HCF_BARD_NOTENOUGHENERGY("HCF.BARD_NOTENOUGHENERGY", "&7You do not have enough energy for this! You need &c<cost> &7energy, but you only have &c<energy>&7!"),
    HCF_ROUGE_BACKSTABBED("HCF.ROUGE_BEENBACKSTABBED", "&c<attacker> &ehas backstabbed you!"),
    HCF_ROUGE_BACKSTABBER("HCF.ROGUE_HAVEBACKSTABBED", "&eYou have backstabbed &c<target>&e."),

    MATCH_HCF_STARTMESSAGE("MATCH.HCF_START_MESSAGE", Arrays.asList("", "&c&lHCF Match&7!", "", "&7Pick between &cBard&7, &cArcher&7, &cRogue&7 and &cDiamond", "&7Kits and Fight to the death to &cWin!", "")),
    MATCH_TEAM_STARTMESSAGE("MATCH.TEAM_START_MESSAGE", Arrays.asList("&c&lMatch Found!", "", "&fYou are playing on &c<arena>&f on a &cTeam Match!")),
    MATCH_SOLO_STARTMESSAGE("MATCH.SOLO_START_MESSAGE", Arrays.asList("&c&lMatch Found!", "", "&fYou are playing on &c<arena>&f with &fPlayers: &c<player1> &7vs &c<player2>")),
    MATCH_ROUND_MESSAGE("MATCH.ROUND_MESSAGE", Arrays.asList(" &c&lRound #<round_number>", "  &fYour Points: &c<your_points>", "  &fTheir Points: &c<their_points>")),
    MATCH_DISCLAIMER("MATCH.DISCLAIMER_MESSAGE", Collections.singletonList("&c&lReminder: &fButterfly clicking is &cdiscouraged &fand could result in a &cban. Use at your own risk.")),
    MATCH_COUNTDOWN("MATCH.COUNTDOWN", "&fStarting in &c<seconds>&f..."),
    MATCH_ROUND_COUNTDOWN("MATCH.ROUND_COUNTDOWN", "&c<seconds>&f..."),
    MATCH_STARTED("MATCH.STARTED", "&aMatch Started!"),
    MATCH_ROUND("MATCH.ROUND_STARTED", "&aThe Round has Started!"),
    MATCH_SPECTATE("MATCH.SPECTATE_JOIN", "&c<spectator> &eis now spectating your match!"),
    MATCH_STOPSPEC("MATCH.SPECTATE_LEAVE", "&c<spectator> &eis no longer spectating your match!"),
    MATCH_INVENTORY_HOVER("MATCH.INVENTORY_HOVER", "&7Click to view &c<inventory_name>'s &7inventory."),
    MATCH_CHECKPOINT("MATCH.CHECKPOINT_ACCQUIRED", "&8[&cParkour&8] &cCheckpoint Accquired!"),
    MATCH_DISCONNECTED("MATCH.PARTICIPANT_DISCONNECTED", "<relation_color><participant_name> &7has disconnected."),
    MATCH_WON("MATCH.PARTICIPANT_WON", "<relation_color><participant_name> &ahas won!"),
    MATCH_DIED("MATCH.PARTICIPANT_DIED", "<relation_color><participant_name> &7has died!"),
    MATCH_KILLED("MATCH.PARTICIPANT_KILLED", "<relation_color_dead><dead_name> &7was killed by <relation_color_killer><killer_name>&7."),
    MATCH_MAX_BUILD("MATCH.MAX_BUILD_LIMIT", "&cYou have reached the build height limit!"),
    MATCH_BOW_HIT("MATCH.BOW_HIT", "&c<damaged_name> &7is now at &c<damaged_health> &4❤"),
    MATCH_PEARL_COOLDOWN("MATCH.PEARL_COOLDOWN", "&cYou are on pearl cooldown for <cooldown>!"),
    MATCH_BOW_COOLDOWN("MATCH.BOW_COOLDOWN", "&cYou are on bow cooldown for <cooldown>!"),
    MATCH_BRIDGE_BLOCK("MATCH.BRIDGE_BLOCK_PLACE", "&cYou cannot place blocks here!"),
    MATCH_BRIDGE_WRONG_PORTAL("MATCH.BRIDGE_WRONG_PORTAL", "&cYou jumped in the wrong portal!"),
    MATCH_BRIDGE_SCORED("MATCH.BRIDGE_SCORED", "<relation_color_scored><scored_name> &fhas scored a Point!"),
    MATCH_BRIDGE_WON("MATCH.BRIDGE_WON_ROUND", Arrays.asList("", "&c<winner_name> &7has won this round!", "")),
    MATCH_INVENTORY_MESSAGE_TITLE("MATCH.INVENTORY_MESSAGE_TITLE", "&c&lMatch Details &7(Click name to view inventory)"),

    TOURNAMENT_ROUND("TOURNAMENT.ROUND_MESSAGE", "&8[&9&lRound&8] &c<round> &7has started!"),
    TOURNAMENT_NOT_PICKED("TOURNAMENT.NOT_PICKED", "&8[&c&lTournament&8] &7You weren't picked this round, please wait for your turn!"),
    TOURNAMENT_ELIMINATED("TOURNAMENT.ELIMINATED", "&8[&c&lTournament&8] &c<eliminated> &7has been eliminated. &8(&c<participants_size>&7/&c<participants_count>&8)"),
    TOURNAMANET_WON("TOURNAMENT.WON", "&8[&c&lTournament&8] &c<won> &7won the &ctournament&7!"),
    TOURNAMENT_JOIN("TOURNAMENT.JOIN", "&8[&c&lTournament&8] &c<joined_party>'s Party &7has joined the tournament! &8(&c<participants_size>/50&8)"),
    TOURNAMENT_LEAVE("TOURNAMENT.LEAVE", "&8[&c&lTournament&8] &c<left_party>'s Party &7has left the tournament! &8(&c<participants_size>/50&8)"),

    EVENT_PREFIX("EVENTS.PREFIX", "&8[&c<event_name>&8] &r"),
    EVENT_JOIN("EVENTS.JOIN", "&c<joined> &7has joined the &c<event_name> Event&8! &8(&c<event_participants_size>/<event_max_players>&8)"),
    EVENT_PLAYER_JOIN("EVENTS.PLAYER_JOIN", "&8[&a+&8] &7You have successfully joined the &c<event_name> Event&8!"),
    EVENT_LEAVE("EVENTS.LEAVE", "&c<left> &7has left the &c<event_name> Event&8! &8(&c<event_participants_size>/<event_max_players>&8)"),
    EVENT_PLAYER_LEAVE("EVENTS.PLAYER_LEAVE", "&8[&c-&8] &7You have successfully left the &c<event_name> Event&8!"),
    EVENT_NOTABLE_JOIN("EVENTS.NOT_ABLE_TO_JOIN", "&7You are not currently able to join the event!"),
    EVENT_ON_GOING("EVENTS.ON_GOING", "&7There is already an active &c<event> Event&7!"),
    EVENT_COOLDOWN_ACTIVE("EVENTS.COOLDOWN_ACTIVE", "&7This event is currently on cooldown, please try again later!"),
    EVENT_ALREADY_STARED("EVENTS.ALREADY_STARTED", "&7This event has already and cannot be joined!"),
    EVENT_ELIMINATED("EVENTS.ELIMINATED", "&c<eliminated_name> &7was eliminated by &c<eliminator_name>&7!"),
    EVENT_DIED("EVENTS.DIED", "&c<eliminated_name> &7has died&7!"),
    EVENT_NO_PERMISSION("EVENTS.NO_PERM", Arrays.asList("&7You do not have permission to use this.", "&7&oPlease upgrade your Rank at &c&ostore.purgecommunity.com &7")),
    EVENT_CANCELLED("EVENTS.CANCELLED", "&cThe <event_name> Event has been cancelled!"),
    EVENT_START_COUNTDOWN("EVENTS.START_COUNTDOWN", "&c<seconds>&f..."),
    EVENT_PARKROUR_WON("EVENTS.PARKOUR_WON", "&c<winner> &ehas reached the end!"),
    EVENT_ROUND_STARTED("EVENTS.ROUND_STARTED", "&cThe round has started!"),
    EVENT_STARTED("EVENTS.STARTED", "&cThe event has started!"),
    EVENT_FULL("EVENTS.FULL", "&cThis event is full on its player capacity!"),
    EVENT_STARTING("EVENTS.STARTING", "&fThe &c<event_name> &fEvent will start in &c10 seconds&f..."),
    EVENT_NOT_ENOUGH_PLAYERS("EVENTS.NOT_ENOUGH_PLAYERS", "&cThere are not enough players to start this event!"),
    EVENT_KILLED("EVENTS.KILLED", "&c<killed_name> &7has died!"),
    EVENT_ANNOUNCE("EVENTS.ANNOUCE", Arrays.asList(" ",
            "&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b",
            "&7\u2b1b\u2b1b&c\u2b1b\u2b1b\u2b1b\u2b1b&7\u2b1b\u2b1b &c&l[<event_name> Event]",
            "&7\u2b1b\u2b1b&c\u2b1b&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b ",
            "&7\u2b1b\u2b1b&c\u2b1b\u2b1b\u2b1b\u2b1b&7\u2b1b\u2b1b &fA &c<event_name> &fevent is being hosted by &c<event_host>",
            "&7\u2b1b\u2b1b&c\u2b1b&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b &fEvent is starting in 60 seconds!",
            "&7\u2b1b\u2b1b&c\u2b1b\u2b1b\u2b1b\u2b1b&7\u2b1b\u2b1b &a&l[Click to Join]",
            "&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b", "")),
    EVENT_HOVER("EVENTS.HOVER", "&7Click to join <event_name> Event"),
    EVENT_WON("EVENTS.WON", Arrays.asList(" ",
            "&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b",
            "&7\u2b1b\u2b1b&c\u2b1b\u2b1b\u2b1b\u2b1b&7\u2b1b\u2b1b &c&l[<event_name> Event]",
            "&7\u2b1b\u2b1b&c\u2b1b&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b ",
            "&7\u2b1b\u2b1b&c\u2b1b\u2b1b\u2b1b\u2b1b&7\u2b1b\u2b1b &c<winner> &ehas won the <event_name> Event.",
            "&7\u2b1b\u2b1b&c\u2b1b&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b &7&oGood game!", "&7\u2b1b\u2b1b&c\u2b1b\u2b1b\u2b1b\u2b1b&7\u2b1b\u2b1b",
            "&7\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b\u2b1b",
            "")),
    
    QUEUE_JOIN_UNRANKED("QUEUE.JOIN_UNRANKED", "&7You have been added to the &c<queue_name> &7queue."),
    QUEUE_JOIN_RANKED("QUEUE.JOIN_RANKED", "&7You have been added to the &c<queue_name> &7queue. &c[<queue_elo>]"),

    DUEL_SENT("DUEL.SENT", "&8[&c&lDuel&8] &fYou sent a duel request to &c<target_name> &7(<target_ping>) &f with kit &c<duel_kit> &fon the arena &c<duel_arena>"),
    DUEL_RECIEVED("DUEL.RECEIVED", "&8[&c&lDuel&8] &c<sender_name> &7(<sender_ping>&7) &fhas sent you a duel request with kit &c<duel_kit> &fon the arena &c<duel_arena>"),
    DUEL_HOVER("DUEL.INVITE_HOVER", "&7Click to accept this duel."),
    DUEL_ACCEPT("DUEL.CLICK_TO_ACCEPT", "&a(Click to Accept)"),

    KITEDITOR_LONG("KITEDITOR.TOO_LONG", "&7A name cannot be longer than &c16 &7characters!"),
    KITEDITOR_RENAMED("KITEDITOR.RENAMED", "&7Successfully &crenamed &7the kit to &c<custom_name>&7!"),


    LEADERBOARDS_KIT_FORMAT("LEADERBOARDS.KIT_FORMAT", "&c<leaderboards_pos> &7&l\uff5c &f<leaderboards_name>: &c<leaderboards_elo> &7(<leaderboards_division>)"),
    LEADERBOARDS_KIT_HEADER("LEADERBOARDS.KIT_HEADER", "&c<kit_name> &7\uff5c &fTop 10"),

    LEADERBOARDS_GLOBAL_FORMAT("LEADERBOARDS.GLOBAL_FORMAT", "&c<leaderboards_pos> &7&l\uff5c &f<leaderboards_name>: &c<leaderboards_elo> &7(<leaderboards_division>)"),
    LEADERBOARDS_GLOBAL_HEADER("LEADERBOARDS.GLOBAL_HEADER", "&cGlobal &7\uff5c &fTop 10"),

    STATS_KIT_LORE("STATS.KIT_LORE", Arrays.asList(CC.MENU_BAR, "&8 \u2022 &cELO: &f<profile_kit_elo>", "&8 \u2022 &cWins: &f<profile_kit_wins>", "&8 \u2022 &cLosses: &f<profile_kit_losses>", CC.MENU_BAR)),
    STATS_KIT_HEADER("STATS.KIT_HEADER", "&c<kit> &7\uff5c &fStats"),
    STATS_GLOBAL_HEADER("STATS.GLOBAL_HEADER", "&cGlobal &7\uff5c &fStats"),
    STATS_GLOBAL_LORE("STATS.GLOBAL_LORE", Arrays.asList(CC.MENU_BAR, "&8 \u2022 &cELO: &f<profile_global_elo>", "&8 \u2022 &cWins: &f<profile_global_wins>", "&8 \u2022 &cLosses: &f<profile_global_losses>", CC.MENU_BAR, "&8 \u2022 &cLeague: &f<profile_elo_division>", "&8 \u2022 &cW/L Ratio: &f<profile_wr_ratio>", CC.MENU_BAR));

    @Getter private final String path;
    @Getter private String value;
    @Getter private List<String> listValue;

    private final BasicConfigurationFile configFile = Array.getInstance().getMessagesConfig();

    Locale(String path, String value) {
        this.path = path;
        this.value = value;
    }

    Locale(String path, List<String> listValue) {
        this.path = path;
        this.listValue = listValue;
    }

    public String toString() {
        Replacement replacement = new Replacement(CC.translate(configFile.getConfiguration().getString(this.path)));
        return replacement.toString().replace("{0}", "\n").replace("|", "┃");
    }

    public List<String> toList() {
        List<String> toReturn = new ArrayList<>();
        for ( String strings : configFile.getConfiguration().getStringList(this.path)) {
            toReturn.add(CC.translate(strings).replace("{0}", "\n"));
        }
        return toReturn;
    }
}
