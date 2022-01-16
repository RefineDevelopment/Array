package xyz.refinedev.practice.event.menu.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.EventSizeMenu;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 12/20/2021
 * Project: Array
 */

public class EventKitButton extends Button {

    private final Array plugin;
    private final FoldersConfigurationFile config;
    private final EventType type;
    private final Kit kit;

    public EventKitButton(Array plugin, EventType type, Kit kit) {
        this.plugin = plugin;
        this.config = plugin.getMenuHandler().getConfigByName("general");

        this.type = type;
        this.kit = kit;
    }

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
        player.closeInventory();

        EventSizeMenu menu = new EventSizeMenu(plugin, type, kit);
        menu.openMenu(player);
    }
}
