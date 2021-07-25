package xyz.refinedev.practice.adapters;

import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.essentials.meta.NametagMeta;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.nametags.construct.NameTagInfo;
import xyz.refinedev.practice.util.nametags.provider.NameTagProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/22/2021
 * Project: Array
 */

public class NameTagAdapter extends NameTagProvider {

    public NameTagAdapter() {
        super("Main", 1);
    }
    public NametagMeta meta = Essentials.getNametagMeta();

    /**
     * Get the Target's Nametag for Viewer
     *
     * @param target The player whose nametag is being fetched
     * @param viewer The player who is viewing the nametag
     * @return {@link NameTagInfo}
     */
    @Override
    public NameTagInfo fetchNameTag(Player target, Player viewer) {
        if (meta.isEnabled()) {
            Profile targetProfile = Profile.getByPlayer(target);
            Profile viewerProfile = Profile.getByPlayer(viewer);

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
            return createNameTag(meta.getPartyColor().toString(), "");
        }
        if (targetProfile.getParty() != null && targetProfile.getParty().containsPlayer(viewer)) {
            return createNameTag(meta.getPartyColor().toString(), "");
        }
        String color = meta.getDefaultColor().equals("<rank_color>") ? targetProfile.getColor().toString() : ChatColor.valueOf(meta.getDefaultColor()).toString();
        return createNameTag(color, "");
    }

    public NameTagInfo getEventColor(Profile viewerProfile, Profile targetProfile) {
        Player viewer = viewerProfile.getPlayer();
        Player target = targetProfile.getPlayer();
        Event event = targetProfile.getEvent();

        if (targetProfile.isInEvent()) {
            return createNameTag(meta.getEventColor().toString(), "");
        }
        return createNameTag("", "");
    }
}
