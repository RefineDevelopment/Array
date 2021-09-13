package xyz.refinedev.practice.clan;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/8/2021
 * Project: Array
 */

@Getter @Setter
public class Clan {

    private final List<ClanProfile> members;
    private final List<ClanProfile> captains;
    private final List<UUID> bannedPlayers;

    private final String name;
    private final UUID uniqueId;

    private ClanProfile leader;
    private String description = "This is the default Description, use /clan setdesc <text> to setup the description for your clan.";
    private String password;
    private String dateCreated;

    private int maxMembers;

    private int elo = 1000;
    private int wins = 0;
    private int losses = 0;
    private int winStreak = 0;
    private int highestWinStreak = 0;

    /**
     * The main Clan Constructor
     *
     * @param name The name of the clan
     * @param leader The UUID of the leader of the clan
     */
    public Clan(String name, UUID leader, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.leader = new ClanProfile(leader, this, ClanRoleType.LEADER);
        this.dateCreated = DateUtil.getFormattedDate(System.currentTimeMillis());

        this.members = new ArrayList<>();
        this.captains = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();

        this.maxMembers = 25;

        Profile profile = Profile.getByUuid(leader);
        profile.setClan(this);
    }

    /**
     * Returns {@link List<ClanProfile>} of all
     * Members of a Clan
     */
    public List<ClanProfile> getAllMembers() {
        List<ClanProfile> list = new ArrayList<>();

        list.addAll(members);
        list.addAll(captains);
        list.add(leader);

        return list;
    }

    /**
     * Get a list of all online members
     * of this clan
     *
     * @return {@link List<Player>} of Online Members
     */
    public List<Player> getOnlineMembers() {
        return getAllMembers().stream().map(ClanProfile::getUuid).map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Broadcast message to all members of the clan
     *
     * @param message The message to broadcast
     */
    public void broadcast(String message) {
        this.getAllMembers().stream().map(ClanProfile::getUuid).map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> player.sendMessage(CC.translate(message)));
    }

    /**
     * Returns true if the given uuid
     * matches the uuid of this Clan's leader
     *
     * @param uuid {@link UUID} the given uuid
     * @return {@link Boolean}
     */
    public boolean isLeader(UUID uuid) {
        return this.getLeader().getUuid().equals(uuid);
    }

    /**
     * Returns true if the given uuid
     * matches the uuid of any captain's uuid
     *
     * @param uuid {@link UUID} the given uuid
     * @return {@link Boolean}
     */
    public boolean isCaptain(UUID uuid) {
        return this.getCaptains().stream().map(ClanProfile::getUuid).anyMatch(unique -> unique.equals(uuid));
    }
}
