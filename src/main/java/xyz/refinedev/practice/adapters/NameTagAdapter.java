package xyz.refinedev.practice.adapters;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.config.ConfigHandler;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
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
     * Get the Target's Nametag for Viewer
     *
     * @param target The player whose nametag is being fetched
     * @param viewer The player who is viewing the nametag
     * @return {@link NameTagInfo}
     */
    @Override
    public NameTagInfo fetchNameTag(Player target, Player viewer) {
            Profile targetProfile = plugin.getProfileManager().getProfile(target);
            Profile viewerProfile = plugin.getProfileManager().getProfile(viewer);

            if ((plugin.getConfigHandler().isNAMETAGS_ENABLED()) && viewerProfile.isInLobby() || viewerProfile.isInQueue()) {
                return this.getNormalColor(viewerProfile, targetProfile);
            } else if (viewerProfile.isInFight() || viewerProfile.isSpectating()) {
                return this.getFightColor(viewerProfile, targetProfile);
            } else if (viewerProfile.isInEvent()) {
                return this.getEventColor(viewerProfile, targetProfile);
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
        ConfigHandler configHandler = this.plugin.getConfigHandler();

        Player viewer = viewerProfile.getPlayer();
        Player target = targetProfile.getPlayer();

        Party viewerParty = this.plugin.getPartyManager().getPartyByUUID(viewerProfile.getParty());
        Party targetParty = this.plugin.getPartyManager().getPartyByUUID(targetProfile.getParty());

        if (viewerParty != null && viewerParty.containsPlayer(target)) {
            return createNameTag(configHandler.getPartyColor().toString(), "");
        }
        if (targetParty != null && targetParty.containsPlayer(viewer)) {
            return createNameTag(configHandler.getPartyColor().toString(), "");
        }
        String color = configHandler.getDefaultColor().equals("<rank_color>") ? plugin.getProfileManager().getColor(targetProfile).toString() : ChatColor.valueOf(configHandler.getDefaultColor()).toString();
        return createNameTag(color, "");
    }

    public NameTagInfo getEventColor(Profile viewerProfile, Profile targetProfile) {
        Player viewer = viewerProfile.getPlayer();
        Player target = targetProfile.getPlayer();
        //UUID event = targetProfile.getEvent();

        if (targetProfile.isInEvent()) {
            return createNameTag(plugin.getConfigHandler().getEventColor().toString(), "");
        }
        return createNameTag("", "");
    }
}
