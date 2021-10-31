package xyz.refinedev.practice.profile.divisions.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.divisions.ProfileDivision;
import xyz.refinedev.practice.util.menu.Button;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ProfileDivisionsButton extends Button {

    private final Array plugin = this.getPlugin();
    private final Profile profile;
    private final ProfileDivision division;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileDivision profileDivision = plugin.getProfileManager().getDivision(profile);

        boolean equipped = division.equals(profileDivision);
        boolean unlocked = division.getMaxElo() < profileDivision.getMinElo() || division.getExperience() < profileDivision.getExperience();
        boolean locked = !equipped && !unlocked;

        //if ()

        return null;
    }
}
