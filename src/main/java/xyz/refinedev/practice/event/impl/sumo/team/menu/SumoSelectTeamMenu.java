package xyz.refinedev.practice.event.impl.sumo.team.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.impl.sumo.team.SumoTeam;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/30/2021
 * Project: Array
 */

public class SumoSelectTeamMenu extends PaginatedMenu {

    /**
     * @param player player viewing the inventory
     * @return title of the inventory before the page number is added
     */
    @Override
    public String getPrePaginatedTitle(Player player) {
        super.setAutoUpdate(true);
        return CC.translate("&7Select a Team");
    }

    /**
     * @param player player viewing the inventory
     * @return a map of button that will be paginated and spread across pages
     */
    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        for ( EventGroup eventGroup : ((SumoTeam) Array.getInstance().getEventManager().getActiveEvent()).getTeams()) {
            buttons.put(i++, new SelectTeamButton(eventGroup));
        }
        return buttons;
    }

    @RequiredArgsConstructor
    private final static class SelectTeamButton extends Button {

        private final EventGroup group;
        
        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&b&lPlayers &7(" + this.group.getPlayers().size() + "/" + this.group.getMaxMembers() + ")");
            if (this.group.getPlayers().size() == 0) {
                lore.add(" &fNo-one");
            } else {
                for ( EventPlayer eventTeamPlayer : this.group.getPlayers()) lore.add(" &f" + eventTeamPlayer.getUsername());
            }
            lore.add("");
            return new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&b&l" + this.group.getColor().getTitle()).color(this.group.getColor().getColor()).lore(lore).amount(this.group.getPlayers().size()).clearFlags().build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            SumoTeam sumoTeam = (SumoTeam) Array.getInstance().getEventManager().getActiveEvent();
            if (sumoTeam == null) return;
            
            if (this.group.getPlayers().size() >= this.group.getMaxMembers()) {
                player.sendMessage(CC.translate("&cThis team is full!"));
            } else {
                EventTeamPlayer eventTeamPlayer = sumoTeam.getEventTeamPlayers().get(player.getUniqueId());
                sumoTeam.getTeams().stream().filter(team -> team.getPlayers().contains(eventTeamPlayer)).forEach(team -> team.removePlayer(eventTeamPlayer));
                this.group.addPlayer(eventTeamPlayer);
                player.sendMessage(CC.translate("&fYou have joined team &b&l" + this.group.getColor().getTitle()));
            }
        }
    }
}
