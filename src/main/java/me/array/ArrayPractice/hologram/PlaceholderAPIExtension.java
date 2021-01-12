package me.array.ArrayPractice.hologram;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlaceholderAPIExtension extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "practice";
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

        if (identifier.contains("global")) { //practice_global_1
            String[] splittedShit=identifier.split("_");
            int number=Integer.parseInt(splittedShit[1]) - 1;
            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards=Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            return "&b#" + (number + 1) + " &8- &f" + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo();
        }

        if (identifier.contains("lb")) { //practice_lb_NoDebuff_1
            String[] splittedShit=identifier.split("_");
            String kitString=splittedShit[1];
            int number=Integer.parseInt(splittedShit[2]) - 1;
            Kit kit=Kit.getByName(kitString);

            if (kit == null) return "&7";

            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards=kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            return "&b#" + (number + 1) + " &8- &f" + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo();
        }

        if (identifier.contains("switch")) { //practice_switch_NoDebuff_1
            String[] splittedShit=identifier.split("_");
            int number=Integer.parseInt(splittedShit[2]) - 1;
            Kit kit=Kit.getByName(getNextLadder());

            if (kit == null) return "&7";

            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards=kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            return "&b#" + (number + 1) + " &8- &f" + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo();
        }

        return null;
    }

    public String getNextLadder() {
        if (this.getIdentifier().contains("switch")) {
            String[] splittedShit=this.getIdentifier().split("_");
            String kitString=splittedShit[1];
            switch (kitString) {
                case "NoDebuff":
                    return "Debuff";
                case "Debuff":
                    return "Gapple";
                case "Gapple":
                    return "Combo";
                case "Combo":
                    return "BuildUHC";
                case "BuildUHC":
                    return "BoxFight";
                case "BoxFight":
                    return "Classic";
                case "Classic":
                    return "AxePvP";
                case "AxePvP":
                    return "Vanilla";
                case "Vanilla":
                    return "Soup";
                case "Soup":
                    return "SoupRefill";
                case "SoupRefill":
                    return "NoDebuff";
                default:
                    return "";
            }
        }
        return null;
    }
}
