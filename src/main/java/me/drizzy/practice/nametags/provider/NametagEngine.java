package me.drizzy.practice.nametags.provider;

import me.drizzy.practice.Array;
import me.drizzy.practice.essentials.meta.NametagMeta;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.nametags.construct.NametagInfo;
import me.drizzy.practice.nametags.provider.NametagProvider;
import me.drizzy.practice.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NametagEngine extends NametagProvider {

    public NametagEngine() {
        super("NametagEngine", 1);
    }

    public NametagMeta meta = Array.getInstance().getEssentials().getNametagMeta();

    @Override
    public NametagInfo fetchNametag(Player target, Player viewer) {
        Profile targetProfile = Profile.getByPlayer(target);
        Profile viewerProfile = Profile.getByPlayer(viewer);

        if (viewerProfile.isInLobby() || viewerProfile.isInQueue()) {
            if (viewerProfile.getParty() != null && viewerProfile.getParty().containsPlayer(target)) {
                return createNametag(meta.getPartyColor().toString(), "");
            }
            if (targetProfile.getParty() != null && targetProfile.getParty().containsPlayer(viewer)) {
                return createNametag(meta.getPartyColor().toString(), "");
            }
        } else if (viewerProfile.isInFight()) {
            TeamPlayer targetTeamPlayer = viewerProfile.getMatch().getTeamPlayer(target);
            if (targetTeamPlayer != null) {
                if (targetTeamPlayer.isAlive() && !targetTeamPlayer.isDisconnected()) {
                    return createNametag(viewerProfile.getMatch().getRelationColor(viewer, target).toString(), "");
                }
            }
        } else if (viewerProfile.isInEvent()) {
            if (targetProfile.isInEvent()) {
                return createNametag(meta.getEventColor().toString(), "");
            }
        } else if (viewerProfile.isSpectating()) {
            TeamPlayer targetTeamPlayer = viewerProfile.getMatch().getTeamPlayer(target);

            if (targetTeamPlayer != null) {
                if (targetTeamPlayer.isAlive() && !targetTeamPlayer.isDisconnected()) {
                    return createNametag(viewerProfile.getMatch().getRelationColor(viewer, target).toString(), "");
                }
            }
        }

        String color = meta.getDefaultColor().equals("<rank_color>") ? targetProfile.getColor() : ChatColor.valueOf(meta.getDefaultColor()).toString();

        return createNametag(color, "");
    }
}