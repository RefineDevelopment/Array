package xyz.refinedev.practice.event.menu.buttons;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.menu.EventKitMenu;
import xyz.refinedev.practice.event.menu.EventSizeMenu;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.ButtonUtil;
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
public class EventSelectButton extends Button {

    private final FoldersConfigurationFile config;
    private final EventType eventType;

    public EventSelectButton(Array plugin, EventType eventType) {
        super(plugin);

        this.config = this.getPlugin().getMenuHandler().getConfigByName("event_host");
        this.eventType = eventType;
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        String path = "BUTTONS." + eventType.name() + ".";

        Material material = ButtonUtil.getMaterial(config, path + "MATERIAL");
        if (material == null) player.closeInventory();

        ItemBuilder itemBuilder = new ItemBuilder(material);
        itemBuilder.name(config.getString(path + "NAME"));

        Event event = this.getPlugin().getEventManager().getActiveEvent();

        if (event == null || !event.getType().equals(eventType)) {
            itemBuilder.lore(config.getStringList(path + "IDLE_LORE"));
            return itemBuilder.build();
        }

        if (event.isFighting()) {
            if (event.getRemainingPlayers().size() == 0) {
                itemBuilder.amount(1);
            } else {
                itemBuilder.amount(event.getRemainingPlayers().size());
            }
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
        player.closeInventory();

        if (eventType.equals(EventType.LMS) || eventType.equals(EventType.BRACKETS)) {
            EventKitMenu kitMenu = new EventKitMenu(this.getPlugin(), eventType);
            kitMenu.openMenu(player);
            Button.playSuccess(player);
            return;
        }

        if (eventType.equals(EventType.SUMO) || eventType.equals(EventType.GULAG)) {
            EventSizeMenu menu = new EventSizeMenu(this.getPlugin(), eventType);
            menu.openMenu(player);
            Button.playSuccess(player);
            return;
        }
        if (!this.getPlugin().getEventManager().hostByType(player, eventType, EventTeamSize.SOLO)) {
            Button.playFail(player);
        }
    }
}
