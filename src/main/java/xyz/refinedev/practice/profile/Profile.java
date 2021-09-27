package xyz.refinedev.practice.profile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.duel.DuelProcedure;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.kiteditor.KitEditor;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.history.MatchHistory;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.rank.TablistRank;
import xyz.refinedev.practice.profile.settings.meta.SettingsMeta;
import xyz.refinedev.practice.profile.statistics.StatisticsData;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueProfile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.*;

@Getter @Setter
public class Profile {

    private final Map<UUID, DuelRequest> sentDuelRequests = new HashMap<>();
    private final Map<Kit, StatisticsData> statisticsData = new LinkedHashMap<>();
    private final List<ClanInvite> clanInviteList = new ArrayList<>();
    private final List<Location> plates = new ArrayList<>();

    private final List<MatchHistory> unrankedMatchHistory = new ArrayList<>();
    private final List<MatchHistory> rankedMatchHistory = new ArrayList<>();

    private final UUID uniqueId;
    private String name;
    private int globalElo = 1000;
    private int kills, deaths, experience;

    private ProfileState state;
    private Party party;
    private Match match;
    private Queue queue;
    private Clan clan;
    private Event event;

    private TablistRank tablistRank;
    private KillEffect killEffect;
    private ClanProfile clanProfile;
    private QueueProfile queueProfile;
    private DuelProcedure duelProcedure;
    private RematchProcedure rematchData;

    private Player spectating;
    private boolean build, silent;

    private Cooldown enderpearlCooldown, bowCooldown, visibilityCooldown;

    private KitEditor kitEditor;
    private SettingsMeta settings;

    private boolean issueRating;
    private Arena ratingArena;

    /**
     * The main constructor for the Profile
     *
     * @param uniqueId The {@link UUID} of the Player
     */
    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.state = ProfileState.IN_LOBBY;
        this.kitEditor = new KitEditor();

        for (Kit kit : Kit.getKits()) {
            this.statisticsData.put(kit, new StatisticsData());
        }
    }

    /**
     * Get's vanilla tablist priority checking
     * through permissions given in the config
     *
     * @return {@link Integer}
     */
    public int getTabPriority() {
        return tablistRank == null ? 0 : tablistRank.getPriority();
    }

    /**
     * Returns true if the specified kill effect is selected
     *
     * @param killEffect {@link KillEffect}
     */
    public boolean isSelected(KillEffect killEffect) {
        return this.killEffect != null && this.killEffect.getUniqueId().equals(killEffect.getUniqueId());
    }

    public Integer getTotalWins() {
        return this.statisticsData.values().stream().mapToInt(StatisticsData::getWon).sum();
    }

    public Integer getTotalLost() {
        return this.statisticsData.values().stream().mapToInt(StatisticsData::getLost).sum();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public boolean isInLobby() {
        return state == ProfileState.IN_LOBBY;
    }

    public boolean isInQueue() {
        return state == ProfileState.IN_QUEUE && queue != null && queueProfile != null;
    }

    public boolean isInMatch() {
        return match != null;
    }

    public boolean isInFight() {
        return state == ProfileState.IN_FIGHT && match != null;
    }

    public boolean isSpectating() {
        return state == ProfileState.SPECTATING && (match != null || event != null);
    }

    public boolean isInEvent() {
        return event != null;
    }

    public boolean isInTournament() {
       return Tournament.getCurrentTournament() != null && Tournament.getCurrentTournament().isParticipating(this.uniqueId);
    }

    public boolean isInSomeSortOfFight() {
        return (state == ProfileState.IN_FIGHT && match != null) || (state == ProfileState.IN_EVENT) || (state == ProfileState.IN_BRAWL);
    }

    public boolean isBusy() {
        return isInQueue() || isInFight() || isInEvent() || isSpectating() || isInTournament();
    }

    public boolean hasClan() {
        return clan != null;
    }

    public boolean hasParty() {
        return party != null;
    }
}
