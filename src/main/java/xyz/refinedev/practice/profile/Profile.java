package xyz.refinedev.practice.profile;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.duel.DuelProcedure;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.kiteditor.KitEditor;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.history.ProfileHistory;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.rank.TablistRank;
import xyz.refinedev.practice.profile.settings.ProfileSettings;
import xyz.refinedev.practice.profile.statistics.ProfileStatistics;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.*;

@Getter @Setter
@RequiredArgsConstructor
public class Profile {

    private transient Map<UUID, DuelRequest> duelRequests = new HashMap<>();
    private transient List<Location> parkourCheckpoints = new ArrayList<>();

    private final Map<Kit, ProfileStatistics> statisticsData = new LinkedHashMap<>();
    private final List<ProfileHistory> unrankedMatchHistory = new ArrayList<>();
    private final List<ProfileHistory> rankedMatchHistory = new ArrayList<>();

    @SerializedName("uuid")
    private final UUID uniqueId;
    private String name;
    private KillEffect killEffect = KillEffect.NONE;
    private int globalElo = 1000;
    private int kills, deaths, experience;
    private ProfileState state = ProfileState.IN_LOBBY;

    private transient UUID tournament, party, queue; //, event, match, queue;
    private UUID clan;

    //TODO turn into uniqueId
    private Match match;

    private TablistRank tablistRank;
    private DuelProcedure duelProcedure;
    private RematchProcedure rematchData;

    private Arena ratingArena;

    //TODO: Have a spectating manager for better features
    private Player spectating;

    private boolean build, silent, issueRating;

    //TODO: Completely Change this shit.
    private Cooldown visibilityCooldown = new Cooldown(0);

    private KitEditor kitEditor = new KitEditor();
    private ProfileSettings settings = new ProfileSettings();

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
        return this.killEffect != null && this.killEffect.equals(killEffect);
    }

    /**
     * Add XP to the profile
     *
     * @param experience {@link Integer} xp amount being added
     */
    public void addExperience(int experience) {
        this.setExperience(experience);

        if (this.getPlayer() == null) return;
        this.getPlayer().sendMessage(Locale.XP_ADD.toString().replace("<xp>", String.valueOf(experience)));
    }

    /**
     * Returns the total amount of wins of this profile
     *
     * @return {@link Integer} total wins
     */
    public int getTotalWins() {
        return this.statisticsData.values().stream().mapToInt(ProfileStatistics::getWon).sum();
    }

    /**
     * Returns the total amount of losses of this profile
     *
     * @return {@link Integer} total losses
     */
    public int getTotalLost() {
        return this.statisticsData.values().stream().mapToInt(ProfileStatistics::getLost).sum();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public boolean isInLobby() {
        return state == ProfileState.IN_LOBBY;
    }


    public boolean isInQueue() {
        return state == ProfileState.IN_QUEUE && queue != null;
    }

    public boolean isInMatch() {
        return match != null;
    }

    public boolean isInFight() {
        return state == ProfileState.IN_FIGHT && this.isInMatch();
    }

    public boolean isSpectating() {
        return state == ProfileState.SPECTATING && (this.isInMatch() || this.isInEvent());
    }

    public boolean isInSomeSortOfFight() {
        return (state == ProfileState.IN_FIGHT && match != null) || (state == ProfileState.IN_EVENT);
    }

    public boolean isBusy() {
        return this.isInQueue() || this.isInFight() || this.isInEvent() || this.isSpectating() || this.isInTournament();
    }

    public boolean isInEvent() {
        return false;
    }

    public boolean isInTournament() {
        return tournament != null;
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean hasClan() {
        return clan != null;
    }
}
