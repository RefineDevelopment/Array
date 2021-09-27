package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.divisions.Division;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/19/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class DivisionsManager {

    private final Array plugin;
    private final BasicConfigurationFile config;

    private final List<Division> divisions = new ArrayList<>();
    private boolean XPBased;

    public void init() {
        if (!divisions.isEmpty()) divisions.clear();

        ConfigurationSection divisionSection = config.getConfigurationSection("DIVISIONS");
        if (divisionSection == null || divisionSection.getKeys(false).isEmpty()) return;
        this.XPBased = divisionSection.getBoolean("XP-BASED");

       ConfigurationSection section = divisionSection.getConfigurationSection("RANKS");
       if (section == null || section.getKeys(false).isEmpty()) return;

       for ( String key : section.getKeys(false) ) {
           String path = key + ".";

           Division division = new Division(key);
           division.setDisplayName(CC.translate(section.getString(path + "DISPLAY_NAME")));
           division.setMinElo(section.getInt(path + "MIN-Elo"));
           division.setMaxElo(section.getInt(path + "MAX-Elo"));
           division.setExperience(section.getInt(path + "XP-AMOUNT"));
           division.setXpLevel(section.getInt(path + "XP-LEVEL"));

           //In order to prevent more than one default division
           if (!this.isDefaultPresent()) division.setDefaultDivision(section.getBoolean(path + "DEFAULT"));

           divisions.add(division);
       }
    }

    /**
     * Get a Profile's Division by their global XP
     *
     * @param xp {@link Integer} the profile's XP
     * @return {@link Division} the profile's division
     */
    public Division getDivisionByXP(int xp) {
        if (this.getHighest() != null && xp > this.getHighest().getExperience()) return this.getHighest();

        List<Division> xpDivisions = new ArrayList<>(divisions);
        xpDivisions.sort(Comparator.comparing(Division::getExperience).reversed());

        return xpDivisions.stream().filter(level -> xp >= level.getExperience()).findFirst().orElse(getDefault());
    }

    /**
     * Get a Profile's Division by their global ELO
     *
     * @param elo {@link Integer} the profile's elo
     * @return {@link Division} the profile's division
     */
    public Division getDivisionByELO(int elo) {
        if (this.getHighest() != null && elo > this.getHighest().getMaxElo()) return this.getHighest();

        for (Division eloRank : divisions) {
            if (elo >= eloRank.getMinElo() && elo <= eloRank.getMaxElo()) {
                return eloRank;
            }
        }
        return getDefault();
    }

    /**
     * Get the default {@link Division}
     *
     * @return {@link Division} default division
     */
    public Division getDefault() {
        return divisions.stream().filter(Division::isDefaultDivision).findAny().orElse(new Division("Default"));
    }

    /**
     * This gets the highest ranking division
     * either based on ELO or XP
     *
     * @return {@link Division} highest rank
     */
    public Division getHighest() {
        LinkedList<Division> newDivisions = new LinkedList<>(divisions);

        if (this.XPBased) {
            newDivisions.sort(Comparator.comparingInt(Division::getExperience).reversed());
        } else {
            newDivisions.sort(Comparator.comparingInt(Division::getMaxElo).reversed());
        }

        return newDivisions.getFirst();
    }

    /**
     * Checks if there is already a default
     * division or not present in the list
     *
     * @return {@link Boolean}
     */
    public boolean isDefaultPresent() {
        if (divisions.isEmpty()) return false;
        return divisions.stream().anyMatch(Division::isDefaultDivision);
    }
}
