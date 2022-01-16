package xyz.refinedev.practice.event.menu.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/8/2021
 * Project: Array
 */

public class EventSizeButton extends Button {

    private final Array plugin;
    private final EventType eventType;
    private final EventTeamSize teamSize;
    private final FoldersConfigurationFile config;
    private final Kit kit;

    public EventSizeButton(Array plugin, EventType eventType, EventTeamSize teamSize, FoldersConfigurationFile config, Kit kit) {
        super(plugin);

        this.plugin = plugin;
        this.eventType = eventType;
        this.teamSize = teamSize;
        this.config = config;
        this.kit = kit;
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    public ItemStack getButtonItem(Player player) {
        String path = "BUTTONS." + teamSize.name() + ".";

        Material material = Material.valueOf(config.getString(path + "MATERIAL"));
        ItemBuilder itemBuilder = new ItemBuilder(material);
        itemBuilder.name(config.getString(path + "NAME"));
        if (config.contains(path + "DATA")) {
            itemBuilder.durability(config.getInteger(path + "DATA"));
        }
        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();
        if (kit != null) {
            if (!plugin.getEventManager().hostByTypeAndKit(player, eventType, teamSize, kit)) {
                Button.playFail(player);
            }
        }

        if (!plugin.getEventManager().hostByType(player, eventType, teamSize)) {
            Button.playFail(player);
        }
    }
}
