package xyz.refinedev.practice.tournament.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.tournament.TournamentProcedure;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.Arrays;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/31/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentKitButton extends Button {

    private final FoldersConfigurationFile config = this.getPlugin().getMenuManager().getConfigByName("general");
    private final Kit kit;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(kit.getDisplayIcon());
        itemBuilder.lore(config.getStringList("TOURNAMENT_KIT_MENU.LORE"));
        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Player player, ClickType clickType) {
        TournamentProcedure procedure = new TournamentProcedure(player, kit);
    }
}
