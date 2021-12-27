package xyz.refinedev.practice.party;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PartyHelperUtil;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class Party extends Team {

    private final Map<UUID, String> kits = new HashMap<>();
    private final List<PartyInvite> invites = new ArrayList<>();
    private final List<UUID> banned = new LinkedList<>();

    private final UUID uniqueId;

    private int limit = 10;

    private boolean isPublic;
    private boolean inTournament;
    private boolean disbanded;

    /**
     * Create a new Party for a Player
     * and assign him as the leader
     *
     * @param player The Leader of the party
     */
    public Party(Player player) {
        super(new TeamPlayer(player.getUniqueId(), player.getName()));
        this.uniqueId = UUID.randomUUID();
        this.kits.put(player.getUniqueId(), PartyHelperUtil.getRandomClass());

        if (player.hasPermission("array.donator")) {
            this.limit = 50;
        }
    }

    /**
     * Update the party's privacy type
     *
     * @param privacy True or false
     */
    public void setPublic(boolean privacy) {
        this.isPublic = privacy;
        this.broadcast(Locale.PARTY_PRIVACY.toString().replace("<privacy>", this.getPrivacy()));
    }

    public String getPrivacy() {
        return (isPublic ? "&aOpen" : "&eClose");
    }

    /**
     * Get a Party Invite from a player's UUID
     *
     * @param uuid The Player's UUID
     * @return {@link PartyInvite}
     */
    public PartyInvite getInvite(UUID uuid) {
        return this.invites.stream().filter(invite -> invite.getUniqueId().equals(uuid)).findAny().orElse(null);
    }

    /**
     * Send Party information message to the specified player
     *
     * @param player The player receiving the information
     */
    public void sendInformation(Player player) {
        String members = "None";

        if (this.getPlayers().size() != 1) {
            members = this.getPlayers().stream().map(Player::getName).collect(Collectors.joining(", "));
        }

        String finalMembers = members;

        for ( String string : Locale.PARTY_INFO.toList() ) {
            String replaced = string
                    .replace("<party_leader_name>", this.getLeader().getUsername())
                    .replace("<party_privacy>", getPrivacy())
                    .replace("<party_members_formatted>", CC.GRAY + "(" + (this.getTeamPlayers().size() - 1) + ") " + finalMembers)
                    .replace("<party_members>", String.valueOf(getTeamPlayers().size()));

            player.sendMessage(CC.translate(replaced));
        }
    }

    public String getName() {
        return this.getLeader().getUsername() + "'s Party";
    }

    public boolean isMember(UUID uuid) {
       return this.getPlayers().stream().map(Player::getUniqueId).anyMatch(id -> id.equals(uuid));
    }
}
