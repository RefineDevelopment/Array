package me.drizzy.practice.placeholders;

import com.allatori.annotations.DoNotRename;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

import java.util.UUID;

@DoNotRename
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

        /**
         * [Originally coded by Drizzy]
         * This placeholder is used to display the viewer's rank
         * name from the Core-Hook.
         *
         * Note: The option for "Core-Hook" must be on in the config
         *
         * @param identifier - %array_opponent%
         */

        if (identifier.contains("displayname")) {
           return Array.getInstance().getRankManager().getFullName(player);
        }

        /**
         * [Originally coded by Drizzy]
         * This placeholder is used to display the viewer's
         * global elo from their kitdata.
         *
         * @param identifier - %array_globalelo%
         */

        if (identifier.contains("globalelo")) {
            return String.valueOf(profile.getGlobalElo());
        }

        /**
         * [Originally coded by Drizzy]
         * This placeholder is used to display the viewer's elo
         * for a certain kit mentioned in the identifier.
         *
         * @param identifier - %array_opponent%
         */

        if (identifier.contains("opponent")) {
            Match match = profile.getMatch();
            if (match == null) {
                return "&7";
            }
            return match.getOpponentPlayer(player).getName();
        }

        /**
         * [Originally coded by Drizzy]
         * This placeholder is used to display the viewer's elo
         * for a certain kit mentioned in the identifier.
         *
         * @param identifier - %array_<kit>_elo%
         */
        if (identifier.contains("elo") && !identifier.contains("global")) {
            String[] splitString = identifier.split("_");
            String kitName = splitString[0];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "&7";
            }
            int elo = profile.getStatisticsData().get(kit).getElo();
            return String.valueOf(elo);
        }

        /**
         * [Originally coded by Drizzy]
         * This placeholder is used to display the viewer's elo
         * for a certain kit mentioned in the identifier.
         *
         * @param identifier - %array_<player>_globalelo%
         */
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

        /**
         * [Originally coded by Drizzy]
         * This placeholder is used to display a player's elo
         * for a certain kit mentioned in the identifier.
         *
         * @param identifier - %array_<player>_<kit>_elo%
         */
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
