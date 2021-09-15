package xyz.refinedev.practice.event.menu.buttons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.EventSizeMenu;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/1/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class EventSelectButton extends Button {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("event_host");
    private final EventType eventType;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        String path = "BUTTONS." + eventType.getEventName().toUpperCase() + ".";

        Material material = Material.valueOf(config.getString(path + "MATERIAL"));
        ItemBuilder itemBuilder = new ItemBuilder(material);
        itemBuilder.name(config.getString(path + "NAME"));

        Event event = plugin.getEventManager().getActiveEvent();

        if (event == null || !event.getType().equals(eventType)) {
            itemBuilder.lore(config.getStringList(path + "IDLE_LORE"));
            return itemBuilder.build();
        }

        List<String> lore = new ArrayList<>();

        config.getStringList(path + "ACTIVE_LORE").forEach(line -> {
            String loreLine = line
                    .replace("<event_state>", event.getState().name())
                    .replace("<event_current_players>", String.valueOf(event.getRemainingPlayers().size()))
                    .replace("<event_max_players>", String.valueOf(event.getMaxPlayers()));

            if (!event.isFreeForAll()) {
                if (event.isTeam()) {
                    loreLine = loreLine
                            .replace("<event_participant_A>", event.getRoundTeamA() == null ? "None" : event.getRoundTeamA().getColor().getTitle())
                            .replace("<event_participant_B>", event.getRoundTeamB() == null ? "None" : event.getRoundTeamB().getColor().getTitle());
                } else {
                    loreLine = loreLine
                            .replace("<event_participant_A>", event.getRoundPlayerA() == null ? "None" : event.getRoundPlayerA().getUsername())
                            .replace("<event_participant_B>", event.getRoundPlayerB() == null ? "None" : event.getRoundPlayerB().getUsername());
                }
            }

            if (event.isWaiting()) {
                if (event.getCooldown() == null) {
                    loreLine = loreLine.replace("<event_interval>", "Waiting for Players");
                } else {
                    String remaining = TimeUtil.millisToSeconds(event.getCooldown().getRemaining());
                    if (remaining.startsWith("-"))
                        remaining = "0.0";

                    loreLine = loreLine.replace("<event_interval>", "Starting in " + remaining);
                }
            } else if (event.isFighting()) {
                loreLine = loreLine.replace("<event_interval>", event.getDuration());
            }

            lore.add(loreLine);
        });

        itemBuilder.lore(lore);


        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking a
     * button on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    public void clicked(Player player, ClickType clickType) {
        if (eventType.getName().contains("Solo") || eventType.getName().contains("Team")) {
            EventSizeMenu menu = new EventSizeMenu();
            menu.openMenu(player);
            Button.playSuccess(player);
            return;
        }
        if (!plugin.getEventManager().hostByType(player, eventType)) {
            player.closeInventory();
            Button.playFail(player);
        }
    }

}
