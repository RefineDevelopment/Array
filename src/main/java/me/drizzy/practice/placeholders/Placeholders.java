package me.drizzy.practice.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Placeholders extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "array";
    }

    @Override
    public String getAuthor() {
        return "Drizzy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "&7";
        }
        Profile profile = Profile.getByUuid(player);

        if (identifier.contains("displayname")) {
            String[] splitString = identifier.split("_");
            String playerName = splitString[0];
            UUID uuid = ArrayCache.getUUID(playerName);
            Profile target = Profile.getByUuid(uuid);
           return Array.getInstance().getRankManager().getFullName(target.getPlayer());
        }

        if (identifier.contains("opponent")) {
            Match match = profile.getMatch();
            String[] splitString = identifier.split("_");
            String playerName = splitString[0];
            UUID uuid = ArrayCache.getUUID(playerName);
            Profile target = Profile.getByUuid(uuid);
            if (match == null) {
                return "&7";
            }
            return match.getOpponentPlayer(target.getPlayer()).getName();
        }

        if (identifier.contains("globalelo")) {
            String[] splitString = identifier.split("_");
            String playerName = splitString[0];
            UUID uuid = ArrayCache.getUUID(playerName);
            Profile target = Profile.getByUuid(uuid);
            if (target == null) {
                return "&7";
            }
            int elo = target.getGlobalElo();
            return String.valueOf(elo);
        }

        if (identifier.contains("elo") && !identifier.contains("global")) {
            String[] splitString = identifier.split("_");
            String kitName = splitString[1];
            String playerName = splitString[0];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "&7";
            }
            UUID uuid = ArrayCache.getUUID(playerName);
            Profile target = Profile.getByUuid(uuid);
            if (target == null) {
                return "&7";
            }
            int elo = target.getStatisticsData().get(kit).getElo();
            return String.valueOf(elo);
        }


        return null;
    }
}
