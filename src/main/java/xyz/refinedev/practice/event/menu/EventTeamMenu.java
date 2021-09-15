package xyz.refinedev.practice.event.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.menu.buttons.EventTeamButton;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/22/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class EventTeamMenu extends PaginatedMenu {

    private final Event event;

    {setAutoUpdate(true);}
    
    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("&7Select a Team");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( EventGroup eventGroup : event.getTeams() ) {
            buttons.put(buttons.size(), new EventTeamButton(event, eventGroup));
        }
        return buttons;
    }
}