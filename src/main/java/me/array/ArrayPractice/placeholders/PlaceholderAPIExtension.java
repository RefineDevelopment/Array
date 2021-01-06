package me.array.ArrayPractice.placeholders;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
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
        if(player == null){
            return "&7";
        }

        if (identifier.contains("global")) { //practice_global_1
            String[] split = identifier.split("_");
            int number = Integer.parseInt(split[1]) - 1;
            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7Not Available";
            }

            if (kitLeaderboards == null) {
                return "&7Not Available";
            }
            UUID player1 = Bukkit.getPlayer(kitLeaderboards.getName()).getUniqueId();
            Profile profile = Profile.getByUuid(player1);
            return "&b#" + (number + 1) + " &8- &7" + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo() + " &8[&r" + profile.getEloLeague() + "&8]";
        }

        if (identifier.contains("lb")) { //practice_lb_NoDebuff_1
            String[] split = identifier.split("_");
            String kitString = split[1];
            int number = Integer.parseInt(split[2]) - 1;
            Kit kit = Kit.getByName(kitString);

            if (kit == null) return "&7";

            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7Not Available";
            }

            if (kitLeaderboards == null) {
                return "&7Not Available";
            }
            UUID player1 = Bukkit.getPlayer(kitLeaderboards.getName()).getUniqueId();
            Profile profile = Profile.getByUuid(player1);
            return "&b#" + (number + 1) + " &8- &7" + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo() + " &8[&r" + profile.getEloLeague() + "&8]";
        }

        return "&7Not Available";
    }
}
