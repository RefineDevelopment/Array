package xyz.refinedev.practice.util.menu.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.custom.action.ActionData;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class ButtonData {

    private final Array plugin = Array.getInstance();

    private final List<ActionData> actions = new ArrayList<>();
    private int slot = 0;
    private ItemStack item;

    public void handleClick(Player player, ClickType clickType) {
        for ( ActionData actionData : actions ) {
            if (actionData == null) return;
            if (actionData.getAction() == null || actionData.getClickType() == null) return;
            if (!actionData.getClickType().equalsIgnoreCase("ALL") && !(ClickType.valueOf(actionData.getClickType())).equals(clickType)) return;

            switch (actionData.getType()) {
                case CLOSE:
                    player.closeInventory();
                    break;
                case COMMAND:
                    player.chat("/" + actionData.getAction());
                    break;
                case MESSAGE:
                    player.sendMessage(CC.translate(actionData.getAction()));
                    break;
                case MENU:
                    player.closeInventory();
                    Menu menu = plugin.getMenuManager().findMenu(player, actionData.getAction());
                    if (menu == null) {
                        player.sendMessage(Locale.ERROR_MENU.toString());
                    } else {
                        menu.openMenu(plugin, player);
                    }
                    break;
            }
        }
    }

}
