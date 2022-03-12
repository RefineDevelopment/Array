package xyz.refinedev.practice.duel.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.duel.DuelProcedure;
import xyz.refinedev.practice.duel.menu.DuelSelectArenaMenu;
import xyz.refinedev.practice.duel.menu.DuelSelectKitMenu;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.ArenaManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/14/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class DuelKitButton extends Button {

    private final DuelSelectKitMenu menu;
    private final Kit kit;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(kit.getDisplayIcon());
        itemBuilder.name(kit.getDisplayName());
        itemBuilder.lore(Arrays.asList("", "&cClick to send a duel with this kit."));
        itemBuilder.clearFlags();
        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param plugin {@link org.bukkit.plugin.Plugin} Array
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = plugin.getProfileManager();
        ArenaManager arenaManager = plugin.getArenaManager();

        Arena arena = arenaManager.getByKit(kit);
        Profile profile = profileManager.getProfile(player.getUniqueId());
        DuelProcedure duelProcedure = profile.getDuelProcedure();

        if (duelProcedure == null) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        duelProcedure.setKit(kit);
        duelProcedure.setArena(arena);

        this.menu.setClosedByMenu(true);
        player.closeInventory();

        if (player.hasPermission("array.duel.arena")) {
            Menu menu  = new DuelSelectArenaMenu();
            plugin.getMenuHandler().openMenu(menu, player);
            return;
        }

        duelProcedure.send();
    }

}
