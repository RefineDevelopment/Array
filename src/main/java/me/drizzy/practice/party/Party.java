package me.drizzy.practice.party;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.duel.DuelRequest;
import me.drizzy.practice.party.enums.PartyPrivacyType;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.nametags.NameTagHandler;
import me.drizzy.practice.util.other.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Party extends Team {

    @Getter private static List<Party> parties = new ArrayList<>();

    private final Map<UUID, String> kits;
    private final List<PartyInvite> invites;
    private final List<Player> banned;

    private PartyPrivacyType privacy;

    private int limit;

    private boolean isPublic;
    private boolean disbanded;

    /**
     * Create a new Party for a Player
     * and assign him as the leader
     *
     * @param player The Leader of the party
     */
    public Party(Player player) {
        super(new TeamPlayer(player.getUniqueId(), player.getName()));

        if (player.hasPermission("array.donator")) {
            this.limit = 50;
        } else {
            this.limit = 10;
        }

        this.isPublic = false;
        this.privacy = PartyPrivacyType.CLOSED;
        this.invites = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.kits = new HashMap<>();
        this.kits.put(player.getUniqueId(), getRandomClass());

        parties.add(this);
    }

    /**
     * Start the essential party tasks
     */
    public static void preLoad() {
        TaskUtil.runTimerAsync(() -> parties.forEach(party -> party.getInvites().removeIf(PartyInvite::hasExpired)), 100L, 100L);
        TaskUtil.runTimerAsync(() -> {
            for (final Party party : Party.getParties()) {
                if (party == null || party.isDisbanded() || party.getPlayers().isEmpty() || party.getLeader() == null ) {
                    return;
                }
                if (party.isPublic()) {
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                        List<String> toSend = new ArrayList<>();
                        toSend.add(Locale.PARTY_PUBLIC.toString().replace("<host>", party.getLeader().getUsername()));
                        toSend.add(Locale.PARTY_CLICK_TO_JOIN.toString());
                        for ( String string : toSend ) {
                            new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + party.getLeader().getUsername()).sendToPlayer(player);
                        }
                    });
                }
            }
        }, 1000L, 1000L);
    }

    /**
     * Update the party's privacy type
     *
     * @param privacy The new PrivacyType
     */
    public void setPrivacy(PartyPrivacyType privacy) {
        this.privacy = privacy;
        this.broadcast(Locale.PARTY_PRIVACY.toString().replace("<privacy>", privacy.toString()));
    }

    /**
     * Get a Party Invite from a player's UUID
     *
     * @param uuid The Player's UUID
     * @return {@link PartyInvite}
     */
    public PartyInvite getInvite(final UUID uuid) {
        for ( PartyInvite invite : this.invites )
            if (invite.getUuid().equals(uuid)) {
                if (invite.hasExpired()) {
                    return null;
                }
                return invite;
            }
        return null;
    }

    /**
     * Invite a specific player to the party
     *
     * @param target The player being in invited
     */
    public void invite(Player target) {

        invites.add(new PartyInvite(target.getUniqueId()));

        List<String> strings = new ArrayList<>();
        strings.add(Locale.PARTY_INVITED.toString().replace("<leader>", getLeader().getUsername()));
        strings.add(Locale.PARTY_CLICK_TO_JOIN.toString());
        strings.forEach(string -> new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + getLeader().getUsername()).sendToPlayer(target));

        this.broadcast(Locale.PARTY_PLAYER_INVITED.toString().replace("<invited>", target.getName()));
    }

    /**
     * Ban the targeted player from the party
     *
     * @param target The player being banned
     */
    public void ban(final Player target) {
        this.banned.add(target);
    }

    /**
     * Unban the targeted player form the party
     *
     * @param target The player being unbanned
     */
    public void unban(final Player target) {
        broadcast(Locale.PARTY_UNBANNED.toString().replace("<target>", target.getName()));
        this.banned.remove(target);
    }

    /**
     * Execute party join for the player
     *
     * @param player The player joining the party
     */
    public void join(final Player player) {
        /*
         * Update their Party
         */
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParty(this);

        this.kits.put(player.getUniqueId(), getRandomClass());

        NameTagHandler.reloadPlayer(player);
        NameTagHandler.reloadOthersFor(player);

        /*
         * Clear Their Invite
         */
        this.invites.removeIf(invite -> invite.getUuid().equals(player.getUniqueId()));

        /*
         * Add to List
         */
        this.getTeamPlayers().add(new TeamPlayer(player));

        /*
         * Broadcast join message to the party
         */
        this.broadcast(Locale.PARTY_PLAYER_JOINED.toString().replace("<joiner>", player.getName()));

        if (profile.isInLobby() || profile.isInQueue()) {
            profile.refreshHotbar();
            profile.handleVisibility();
        }

        for (TeamPlayer teamPlayer : this.getTeamPlayers()) {
            Player otherPlayer = teamPlayer.getPlayer();
            if (otherPlayer != null) {
                Profile teamProfile = Profile.getByUuid(teamPlayer.getUuid());
                teamProfile.handleVisibility(otherPlayer, player);
            }
        }

        Player random = getTeamPlayers().get(0).getPlayer();
        Profile profile1 = Profile.getByPlayer(random);
        if (profile1.isInMatch()) {
            profile1.getMatch().addSpectator(player, random);
        }
    }

    /**
     * Execute party leave tasks for the player leaving
     *
     * @param player The player leaving
     * @param kick If the leave is a forced kick or not
     */
    public void leave(Player player, boolean kick) {

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParty(null);
        this.getTeamPlayers().removeIf(member -> member.getUuid().equals(player.getUniqueId()));
        this.getPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        this.kits.remove(player.getUniqueId());

        if (kick) {
            this.broadcast(Locale.PARTY_PLAYER_KICKED.toString().replace("<leaver>", player.getName()));
        } else {
            this.broadcast(Locale.PARTY_PLAYER_LEFT.toString().replace("<leaver>", player.getName()));
        }

        if (profile.isInLobby() || profile.isInQueue()) {
            NameTagHandler.reloadPlayer(player);
            NameTagHandler.reloadOthersFor(player);

            profile.handleVisibility();
            profile.refreshHotbar();
        }

        /*
         * If the player is in Fight then reset and teleport them to spawn
         */
        if (profile.isInFight()) {
            profile.getMatch().handleDeath(player, null, true);

            if (profile.getMatch().isTeamMatch() || profile.getMatch().isHCFMatch()) {
                for (final TeamPlayer secondTeamPlayer : this.getTeamPlayers()) {
                    if (secondTeamPlayer.isDisconnected()) {
                        continue;
                    }
                    if (secondTeamPlayer.getUuid().equals(player.getUniqueId())) {
                        continue;
                    }
                    final Player secondPlayer = secondTeamPlayer.getPlayer();
                    if (secondPlayer != null) {
                        player.hidePlayer(secondPlayer);
                    }
                    NameTagHandler.reloadPlayer(player);
                    NameTagHandler.reloadOthersFor(player);
                }
            }

            if (profile.isSpectating()) {
                profile.getMatch().removeSpectator(player);
            }
            profile.setState(ProfileState.IN_LOBBY);
            profile.setMatch(null);
            profile.refreshHotbar();
            profile.handleVisibility();
            profile.teleportToSpawn();
        }

        for (final TeamPlayer teamPlayer : this.getTeamPlayers()) {
            final Player otherPlayer = teamPlayer.getPlayer();
            if (otherPlayer != null) {
                final Profile otherProfile = Profile.getByUuid(teamPlayer.getUuid());
                otherProfile.handleVisibility(otherPlayer, player);
            }
        }
        NameTagHandler.reloadPlayer(player);
        NameTagHandler.reloadOthersFor(player);
    }

    /**
     * Make the targeted player, the leader of the party
     *
     * @param player The Original Leader of the Party
     * @param target The New Leader of the Party
     */
    public void leader(Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        for (TeamPlayer teamPlayer : this.getTeamPlayers()) {
            if (teamPlayer.getPlayer().equals(targetProfile.getPlayer())) {
                targetProfile.getParty().setLeader(teamPlayer);
            }
        }

        this.broadcast(Locale.PARTY_PROMOTED.toString().replace("<promoted>", target.getName()));

        if (profile.isInLobby()) {
            profile.refreshHotbar();
        }
        if (targetProfile.isInLobby()) {
            targetProfile.refreshHotbar();
        }
    }

    /**
     * Execute tasks for disbanding the party
     */
    public void disband() {
        this.broadcast(Locale.PARTY_DISBANDED.toString());

        Profile leaderProfile = Profile.getByUuid(this.getLeader().getUuid());
        leaderProfile.getSentDuelRequests().values().removeIf(DuelRequest::isParty);

        this.getPlayers().forEach(player -> {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if ( profile.isInFight() ) {
                profile.getMatch().handleDeath(player, this.getLeader().getPlayer(), true);
            }
            profile.setParty(null);
            if ( profile.isInLobby() || profile.isInQueue() ) {
                profile.refreshHotbar();
                profile.handleVisibility();
                profile.teleportToSpawn();
                NameTagHandler.reloadPlayer(player);
                NameTagHandler.reloadOthersFor(player);
            }
        });

        parties.remove(this);
        this.disbanded = true;

        for (Player partyPlayer : this.getPlayers()) {
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                NameTagHandler.reloadPlayer(partyPlayer, player);
            }
        }
    }

    /**
     * Send Party information message to the specified player
     *
     * @param player The player receiving the information
     */
    public void sendInformation(Player player) {
        StringBuilder builder = new StringBuilder();

        for (Player member : this.getPlayers()) {
            if (this.getPlayers().size() == 1) {
                builder.append(CC.RESET).append("None").append(CC.GRAY).append(", ");
            } else {
                if (member.equals(this.getLeader().getPlayer())) {
                    continue;
                }
                builder.append(CC.RESET).append(member.getName()).append(CC.GRAY).append(", ");
            }
        }

        Locale.PARTY_INFO.toList().forEach(string -> {

            String main = string.replace("<party_leader_name>", this.getLeader().getUsername())
                                .replace("<party_privacy>", this.privacy.toString())
                                .replace("<party_members_formatted>", CC.GRAY + "(" + (this.getTeamPlayers().size() - 1) + ") " + builder.substring(0, builder.length() - 2))
                                .replace("<party_members>", String.valueOf(getTeamPlayers().size()));
            player.sendMessage(CC.translate(main));

        });
    }

    public String getRandomClass() {
        int i = Array.random.nextInt(4);
        int armor = i + 1;

        switch (armor) {
            case 2: return "Archer";
            case 3: return "Bard";
            case 4: return "Rogue";
            default: return "Diamond";
        }
    }
}
