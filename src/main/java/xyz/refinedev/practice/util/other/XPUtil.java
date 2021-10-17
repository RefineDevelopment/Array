package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.profile.history.ProfileHistory;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

@UtilityClass
public class XPUtil {

    /**
     * Returns the experience to add to the profile
     *
     * @param history       {@link ProfileHistory} the kit used in the profile's match
     * @return              {@link Integer} experience
     */
    public int handleExperience(ProfileHistory history) {
        //Initial experience
        int experience = 100;
        MatchSnapshot matchSnapshot = history.isWon() ? history.getPlayerSnapshot() : history.getOpponentSnapshot();

        //Deduct or add the win or loose points
        experience += history.isWon() ? 25 : -25;

        //Don't count the combos and hits if the kit is combo
        if (!history.getKit().getName().contains("Combo")) {
            experience = (int) ((long) (experience + Math.min(matchSnapshot.getTeamPlayer().getLongestCombo(), 25)) + (matchSnapshot.getTeamPlayer().getHits() > 10 ? Math.round((double) matchSnapshot.getTeamPlayer().getHits() / 10.0) : 0L));
        }
        //Obviously deduct points for missed potions
        experience -= matchSnapshot.getTeamPlayer().getPotionsThrown();
        experience -= matchSnapshot.getTeamPlayer().getPotionsMissed() * 3;

        //It takes less effort to win in these matches so give them less points
        experience -= history.getKit().getName().contains("Sumo") || history.getKit().getName().contains("Spleef") ? 50 : 0;

        //If the match was ranked then multiply by 3 cuz ranked takes balls
        if (history.isRanked()) experience = Math.round(experience * 3);

        return experience;
    }
}
