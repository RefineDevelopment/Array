package xyz.refinedev.practice.profile.divisions.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.divisions.ProfileDivision;
import xyz.refinedev.practice.util.chat.ProgressBar;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ProfileXPDivisionsButton extends Button {

    private final Array plugin = this.getPlugin();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("profile_divisions");

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
        boolean unlocked = profileDivision.getExperience() >= division.getExperience();

        String key = "XP.KEY.";

        ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER);

        if (equipped || unlocked) {
            itemBuilder.name(config.getString(key + "NAME".replace("KEY", equipped ? "EQUIPPED" : "UNLOCKED")));
            if (equipped) itemBuilder.enchantment(Enchantment.DURABILITY, 10);
            itemBuilder.lore(config.getStringList(key + "LORE".replace("KEY", equipped ? "EQUIPPED" : "UNLOCKED")).stream().map(s -> {
                s = s.replace("<division_bar>", ProgressBar.getBar(5, 1))
                     .replace("<division_experience>", String.valueOf(division.getExperience()));
                return s;
            }).collect(Collectors.toList()));
        } else {
            itemBuilder.name(config.getString(key + "NAME".replace("KEY", "LOCKED")));
            itemBuilder.lore(config.getStringList(key + "LORE".replace("KEY", "LOCKED")).stream().map(s -> {
                s = s.replace("<division_bar>", ProgressBar.getBar(profileDivision.getExperience(), division.getExperience()))
                     .replace("<division_experience>", String.valueOf(division.getExperience()));
                return s;
            }).collect(Collectors.toList()));
        }
        return itemBuilder.clearFlags().build();
    }
}
