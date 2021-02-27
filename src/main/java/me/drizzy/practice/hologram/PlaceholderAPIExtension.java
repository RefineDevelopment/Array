package me.drizzy.practice.hologram;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitLeaderboards;

public class PlaceholderAPIExtension extends PlaceholderExpansion {
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

        /**
         * Originally coded by Nick and improved by Drizzy.
         * Note: The Player Cache in profile should not be null for it to work!
         *
         * @param GlobalLeaderboards - array_global_<position>
         */

        if (identifier.contains("global")) {
            String[] splitstring = identifier.split("_");
            int number = Integer.parseInt(splitstring[1]) - 1;
            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            Profile profile = Profile.getByUuid(Profile.getUUID(kitLeaderboards.getName()));

            return "&b" + (number + 1) + ". &f" + kitLeaderboards.getName() + ": &b" + kitLeaderboards.getElo() + " &7(" + profile.getEloLeague() + ")";
        }

        /**
         * Originally coded by Nick and improved by Drizzy.
         * Note: The Player Cache in profile should not be null for it to work!
         *
         * @param KitLeaderboards - array_lb_<Kit>_<position>
         */
        if (identifier.contains("lb")) {
            String[] splitstring = identifier.split("_");
            String kitString = splitstring[1];
            int number = Integer.parseInt(splitstring[2]) - 1;
            Kit kit = Kit.getByName(kitString);

            if (kit == null) return "&7";

            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            Profile profile = Profile.getByUuid(Profile.getUUID(kitLeaderboards.getName()));

            return "&b" + (number + 1) + ". &f" + kitLeaderboards.getName() + ": &b" + kitLeaderboards.getElo() + " &7(" + profile.getEloLeague() + ")";
        }
        return null;
    }
}
