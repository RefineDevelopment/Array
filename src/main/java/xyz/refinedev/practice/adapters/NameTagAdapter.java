package xyz.refinedev.practice.adapters;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.nametags.construct.NameTagInfo;
import xyz.refinedev.practice.util.nametags.provider.NameTagProvider;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */

public class NameTagAdapter extends NameTagProvider {

    private final Array plugin;

    public NameTagAdapter(Array plugin) {
        super("Main", 1);

        this.plugin = plugin;
    }

    /**
     * Get the Target's NameTag for Viewer
     *
     * @param target The player whose NameTag is being fetched
     * @param viewer The player who is viewing the NameTag
     * @return {@link NameTagInfo}
     */
    @Override
    public NameTagInfo fetchNameTag(Player target, Player viewer) {
        if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
            Profile targetProfile = plugin.getProfileManager().getByPlayer(target);
            Profile viewerProfile = plugin.getProfileManager().getByPlayer(viewer);

            if (viewerProfile.isInLobby() || viewerProfile.isInQueue()) {
                return this.getNormalColor(viewerProfile, targetProfile);
            } else if (viewerProfile.isInFight() || viewerProfile.isSpectating()) {
                return this.getFightColor(viewerProfile, targetProfile);
            } else if (viewerProfile.isInEvent()) {
                return this.getEventColor(viewerProfile, targetProfile);
            }
        }
        return createNameTag("", "");
    }

    public NameTagInfo getFightColor(Profile viewerProfile, Profile targetProfile) {
        Player viewer = viewerProfile.getPlayer();
        Player target = targetProfile.getPlayer();

        TeamPlayer targetTeamPlayer = viewerProfile.getMatch().getTeamPlayer(target);
        if (targetTeamPlayer != null) {
            if (targetTeamPlayer.isAlive() && !targetTeamPlayer.isDisconnected()) {
                return createNameTag(viewerProfile.getMatch().getRelationColor(viewer, target).toString(), "");
            }
        }
        return createNameTag("", "");
    }

    public NameTagInfo getNormalColor(Profile viewerProfile, Profile targetProfile) {
        Player viewer = viewerProfile.getPlayer();
        Player target = targetProfile.getPlayer();

        if (viewerProfile.getParty() != null && viewerProfile.getParty().containsPlayer(target)) {
            return createNameTag(plugin.getConfigHandler().getPartyColor().toString(), "");
        }
        if (targetProfile.getParty() != null && targetProfile.getParty().containsPlayer(viewer)) {
            return createNameTag(plugin.getConfigHandler().getPartyColor().toString(), "");
        }
        String color = plugin.getConfigHandler().getDefaultColor().equals("<rank_color>") ? plugin.getProfileManager().getColor(targetProfile).toString() : ChatColor.valueOf(plugin.getConfigHandler().getDefaultColor()).toString();
        return createNameTag(color, "");
    }

    public NameTagInfo getEventColor(Profile viewerProfile, Profile targetProfile) {
        Player viewer = viewerProfile.getPlayer();
        Player target = targetProfile.getPlayer();

        if (viewerProfile.isInEvent()) {
            EventPlayer targetEventPlayer = viewerProfile.getEvent().getEventPlayer(target.getUniqueId());
            if (targetEventPlayer != null) {
                return createNameTag(viewerProfile.getEvent().getRelationColor(viewer, target).toString(), "");
            }
            return createNameTag(plugin.getConfigHandler().getEventColor().toString(), "");
        }
        return createNameTag("", "");
    }
}
