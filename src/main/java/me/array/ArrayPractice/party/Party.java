package me.array.ArrayPractice.party;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.duel.DuelRequest;
import me.array.ArrayPractice.util.ChatHelper;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.nametag.NameTags;
import org.bukkit.ChatColor;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import java.util.Iterator;
import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import java.util.List;

public class Party extends Team
{
    private static List<Party> parties;
    private int Limit;
    private boolean isPublic;
    private PartyPrivacy privacy;
    private final List<PartyInvite> invites;
    private final List<Player> banned;
    private boolean disbanded;
    
    public Party(final Player player) {
        super(new TeamPlayer(player.getUniqueId(), player.getName()));
        this.Limit = 10;
        this.isPublic = false;
        this.privacy = PartyPrivacy.CLOSED;
        this.invites = new ArrayList<>();
        this.banned = new ArrayList<>();
        Party.parties.add(this);
        player.sendMessage(CC.translate("&8&m-----------------------------------------"));
        player.sendMessage(CC.translate("&7You have successfully created a new &bParty&7."));
        player.sendMessage(CC.translate("&7To invite your friends do &b/party invite &7."));
        player.sendMessage(CC.translate(" "));
        player.sendMessage(CC.translate("&7&oTo use party chat do '&b&o@&7&o'&7&o <messsage>."));
        player.sendMessage(CC.translate("&7&o/party help for information on all commands."));
        player.sendMessage(CC.translate("&8&m-----------------------------------------"));
    }
    
    public void setPrivacy(final PartyPrivacy privacy) {
        this.privacy = privacy;
        this.broadcast(PartyMessage.PRIVACY_CHANGED.format(privacy.getReadable()));
    }
    
    public PartyInvite getInvite(final UUID uuid) {
        final Iterator<PartyInvite> iterator = this.invites.iterator();
        while (iterator.hasNext()) {
            final PartyInvite invite = iterator.next();
            if (invite.getUuid().equals(uuid)) {
                if (invite.hasExpired()) {
                    iterator.remove();
                    return null;
                }
                return invite;
            }
        }
        return null;
    }
    
    public void invite(final Player target) {
        this.invites.add(new PartyInvite(target.getUniqueId()));
        target.sendMessage(PartyMessage.YOU_HAVE_BEEN_INVITED.format(this.getLeader().getUsername()));
        target.spigot().sendMessage(new ChatComponentBuilder("").parse(PartyMessage.CLICK_TO_JOIN.format()).attachToEachPart(ChatHelper.click("/party join " + this.getLeader().getUsername())).attachToEachPart(ChatHelper.hover(CC.GREEN + "Click to to accept this party invite")).create());
        this.broadcast(PartyMessage.PLAYER_INVITED.format(target.getName()));
    }
    
    public void ban(final Player target) {
        this.banned.add(target);
    }
    
    public void unban(final Player target) {
        this.banned.remove(target);
    }
    
    public void join(final Player player) {
        this.invites.removeIf(invite -> invite.getUuid().equals(player.getUniqueId()));
        this.getTeamPlayers().add(new TeamPlayer(player));
        this.broadcast(PartyMessage.PLAYER_JOINED.format(player.getName()));
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParty(this);
        if (profile.isInLobby() || profile.isInQueue()) {
            profile.refreshHotbar();
            profile.handleVisibility();
        }
        for (final TeamPlayer teamPlayer : this.getTeamPlayers()) {
            final Player otherPlayer = teamPlayer.getPlayer();
            if (otherPlayer != null) {
                NameTags.color(player, otherPlayer, ChatColor.BLUE, false);
                NameTags.color(otherPlayer, player, ChatColor.BLUE, false);
                final Profile teamProfile = Profile.getByUuid(teamPlayer.getUuid());
                teamProfile.handleVisibility(otherPlayer, player);
            }
        }
    }

    public void leave(final Player player, final boolean kick) {
        this.broadcast(PartyMessage.PLAYER_LEFT.format(player.getName(), kick ? "been kicked from" : "left"));
        this.getTeamPlayers().removeIf(member -> member.getUuid().equals(player.getUniqueId()));
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setParty(null);
        if (profile.isInLobby() || profile.isInQueue()) {
            profile.handleVisibility();
            profile.refreshHotbar();
        }
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
                    NameTags.reset(player, secondPlayer);
                }
            }
            player.setFireTicks(0);
            player.updateInventory();
            profile.setState(ProfileState.IN_LOBBY);
            profile.setMatch(null);
            profile.refreshHotbar();
            profile.handleVisibility();
            Practice.get().getEssentials().teleportToSpawn(player);
        }
        for (final TeamPlayer teamPlayer : this.getTeamPlayers()) {
            final Player otherPlayer = teamPlayer.getPlayer();
            if (otherPlayer != null) {
                NameTags.reset(player, otherPlayer);
                NameTags.reset(otherPlayer, player);
                final Profile otherProfile = Profile.getByUuid(teamPlayer.getUuid());
                otherProfile.handleVisibility(otherPlayer, player);
            }
        }
    }
    
    public void leader(final Player player, final Player target) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final Profile targetprofile = Profile.getByUuid(target.getUniqueId());
        for (final TeamPlayer teamPlayer : this.getTeamPlayers()) {
            if (teamPlayer.getPlayer().equals(targetprofile.getPlayer())) {
                targetprofile.getParty().setLeader(teamPlayer);
            }
        }
        if (profile.isInLobby()) {
            profile.refreshHotbar();
        }
        if (targetprofile.isInLobby()) {
            targetprofile.refreshHotbar();
        }
    }

    public void disband() {
        Party.parties.remove(this);
        this.disbanded = true;
        this.broadcast(PartyMessage.DISBANDED.format());
        final Profile leaderProfile = Profile.getByUuid(this.getLeader().getUuid());
        leaderProfile.getSentDuelRequests().values().removeIf(DuelRequest::isParty);
        this.getPlayers().forEach(player -> {
            Profile.getByUuid(player.getUniqueId());
            Profile.getByUuid(player.getUniqueId()).setParty(null);
            if (Profile.getByUuid(player.getUniqueId()).isInLobby() || Profile.getByUuid(player.getUniqueId()).isInQueue()) {
                Profile.getByUuid(player.getUniqueId()).refreshHotbar();
                Profile.getByUuid(player.getUniqueId()).handleVisibility();
                NameTags.reset(player, this.getLeader().getPlayer());
            }
        });
    }
    
    public void sendInformation(final Player player) {
        final StringBuilder builder = new StringBuilder();
        for (final Player member : this.getPlayers()) {
            if (this.getPlayers().size() == 1) {
                builder.append(CC.RESET).append("None").append(CC.GRAY).append(", ");
            }
            else {
                if (member.equals(this.getLeader().getPlayer())) {
                    continue;
                }
                builder.append(CC.RESET).append(member.getName()).append(CC.GRAY).append(", ");
            }
        }
        final String[] lines = { CC.CHAT_BAR, CC.AQUA + "Party Information", CC.AQUA + "Privacy: " + CC.GRAY + this.privacy.getReadable(), CC.AQUA + "Leader: " + CC.RESET + this.getLeader().getUsername(), CC.AQUA + "Members: " + CC.GRAY + "(" + (this.getTeamPlayers().size() - 1) + ") " + builder.substring(0, builder.length() - 2), CC.CHAT_BAR };
        player.sendMessage(lines);
    }
    
    public boolean isDisbanded() {
        return this.disbanded;
    }
    
    public static void init() {
        new BukkitRunnable() {
            public void run() {
                Party.getParties().forEach(party -> party.getInvites().removeIf(PartyInvite::hasExpired));
            }
        }.runTaskTimerAsynchronously(Practice.get(), 100L, 100L);
        new BukkitRunnable() {
            public void run() {
                for (final Party party : Party.getParties()) {
                    if (party.isPublic()) {
                        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(PartyMessage.PUBLIC.format(party.getLeader().getUsername())));
                        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.spigot().sendMessage(new ChatComponentBuilder("").parse(PartyMessage.CLICK_TO_JOIN.format()).attachToEachPart(ChatHelper.click("/party join " + party.getLeader().getUsername())).attachToEachPart(ChatHelper.hover(PartyMessage.CLICK_TO_JOIN.format())).create()));
                    }
                }
            }
        }.runTaskTimerAsynchronously(Practice.get(), 1200L, 1200L);
    }
    
    public PartyPrivacy getPrivacy() {
        return this.privacy;
    }
    
    public List<PartyInvite> getInvites() {
        return this.invites;
    }
    
    public static List<Party> getParties() {
        return Party.parties;
    }
    
    public int getLimit() {
        return this.Limit;
    }
    
    public void setLimit(final int Limit) {
        this.Limit = Limit;
    }
    
    public boolean isPublic() {
        return this.isPublic;
    }
    
    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public List<Player> getBanned() {
        return this.banned;
    }
    
    static {
        Party.parties = new ArrayList<>();
    }
}
