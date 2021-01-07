package me.array.ArrayPractice.placeholders;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPIExtension extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "practice";
    }

    @Override
    public String getAuthor() {
        return "iMoltres";
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
            String[] splittedShit = identifier.split("_");
            int number = Integer.parseInt(splittedShit[1]) - 1;
            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7Not Available &7[&cError&7]";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            Player player1 = Bukkit.getPlayer(kitLeaderboards.getName());
            Profile profile = Profile.getByUuid(player1.getUniqueId());
            String elorank = profile.getEloLeague();

            return "&b#" + (number + 1) + " &8- &7" + Practice.get().getCoreHook().getPlayerPrefix(player1) + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo() + " &8[" + elorank + "&8]" ;
        }

        if (identifier.contains("lb")) { //practice_lb_NoDebuff_1
            String[] splittedShit = identifier.split("_");
            String kitString = splittedShit[1];
            int number = Integer.parseInt(splittedShit[2]) - 1;
            Kit kit = Kit.getByName(kitString);

            if (kit == null) return "&7";

            KitLeaderboards kitLeaderboards;

            try {
                kitLeaderboards = kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7Not Available &7[&cError&7]";
            }

            if (kitLeaderboards == null) {
                return "&7";
            }

            Player player1 = Bukkit.getPlayer(kitLeaderboards.getName());
            Profile profile = Profile.getByUuid(player1.getUniqueId());
            String elorank = profile.getEloLeague();

            return "&b#" + (number + 1) + " &8- &7" + Practice.get().getCoreHook().getPlayerPrefix(player1) + kitLeaderboards.getName() + " &8- &b" + kitLeaderboards.getElo() + " &8[" + elorank + "&8]" ;
        }

        return null;
    }
}
