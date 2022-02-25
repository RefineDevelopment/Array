package xyz.refinedev.practice.duel.menu.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/22/2022
 * Project: Array
 */

public class DuelArenaButton extends Button {

    private final Menu menu;
    private final Arena arena;

    public DuelArenaButton(Array plugin, Menu menu, Arena arena) {
        super(plugin);

        this.menu = menu;
        this.arena = arena;
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(arena.getDisplayIcon())
                .name(arena.getDisplayName())
                .build();
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
        Profile profile = this.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        profile.getDuelProcedure().setArena(arena);
        profile.getDuelProcedure().send();

        menu.setClosedByMenu(true);
        player.closeInventory();
    }

}