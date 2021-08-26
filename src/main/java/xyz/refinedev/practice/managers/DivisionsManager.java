package xyz.refinedev.practice.managers;

import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.divisions.Division;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/19/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class DivisionsManager {

    private final List<Division> divisions = new ArrayList<>();
    private final Array plugin;
    private final BasicConfigurationFile config;

    public void init() {

    }

    public Division getDivisionByXP(int xp) {


        return null;
    }


    public Division getDivisionByELO(int elo) {
        for (Division eloRank : divisions) {
            if (elo >= eloRank.getMinElo() && elo <= eloRank.getMaxElo()) {
                return eloRank;
            }
        }
        return null;
    }
}
