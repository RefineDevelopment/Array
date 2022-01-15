package xyz.refinedev.practice.event.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/22/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class EventTeamButton extends Button {

    private final Event event;
    private final Menu menu;
    private final EventGroup eventGroup;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        if (eventGroup.getPlayers().size() == 0) {
            lore.add("&fNo players");
        } else {
            for ( EventPlayer eventPlayer : eventGroup.getPlayers() ) {
                lore.add(" &f" + eventPlayer.getUsername());
            }
        }
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .color(eventGroup.getColor().getColor())
                .lore(lore)
                .name("&c&l" + eventGroup.getColor().getTitle() + " &7(" + eventGroup.getPlayers().size() + "/" + eventGroup.getMaxMembers() + ")")
                .amount(eventGroup.getPlayers().size())
                .clearFlags().build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        menu.setClosedByMenu(true);
        player.closeInventory();

        if (this.eventGroup.getPlayers().size() >= this.eventGroup.getMaxMembers()) {
            player.sendMessage(CC.translate("&cThis team is full!"));
        } else {
            EventTeamPlayer eventTeamPlayer = event.getEventTeamPlayers().get(player.getUniqueId());

            event.getTeams().stream().filter(team -> team.getPlayers().contains(eventTeamPlayer)).forEach(team -> team.removePlayer(eventTeamPlayer));
            this.eventGroup.addPlayer(eventTeamPlayer);

            player.sendMessage(CC.translate("&fYou have joined team &c&l" + this.eventGroup.getColor().getTitle() + "&7."));
        }

    }
}
