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
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 11/3/2021
 * Project: Array
 */
@RequiredArgsConstructor
public class ProfileELODivisionsButton extends Button {

    private static final String KEY = "PROFILE_DIVISIONS.";

    private final Profile profile;
    private final ProfileDivision division;
    private final BasicConfigurationFile config;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ProfileDivision profileDivision = plugin.getProfileManager().getDivision(profile);

        boolean equipped = division.equals(profileDivision);
        boolean unlocked = division.getMaxElo() < profile.getGlobalElo();

        String key = KEY + "ELO.KEY.";

        ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER);

        if (equipped || unlocked) {
            itemBuilder.name(config.getString(key + "NAME".replace("KEY", equipped ? "EQUIPPED" : "UNLOCKED")));
            itemBuilder.lore(config.getStringList(KEY + "ELO.UNLOCKED.LORE")
                    .stream()
                    .map(s -> {
                         s = s.replace("<division_bar>", ProgressBar.getBar(5, 1))
                              .replace("<division_min_elo>", String.valueOf(division.getMinElo()));
                         return s;
            }).collect(Collectors.toList()));
            if (equipped) itemBuilder.enchantment(Enchantment.DURABILITY, 10);
            itemBuilder.clearFlags();
            return itemBuilder.build();
        }

        itemBuilder.name(config.getString(KEY + "ELO.LOCKED.NAME"));
        itemBuilder.lore(config.getStringList(KEY + "ELO.LOCKED.LORE")
                .stream()
                .map(s -> {
                     s = s.replace("<division_bar>", ProgressBar.getBar(profile.getGlobalElo(), division.getMinElo()))
                          .replace("<division_min_elo>", String.valueOf(division.getMinElo()));
                     return s;
        }).collect(Collectors.toList()));
        return itemBuilder.build();
    }
}
