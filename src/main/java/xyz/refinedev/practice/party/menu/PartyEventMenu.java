package xyz.refinedev.practice.party.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.party.menu.buttons.PartyEventButton;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class PartyEventMenu extends Menu {

    public PartyEventMenu(Array plugin) {
        plugin.getMenuHandler().loadMenu(this, "PARTY-EVENTS");
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return this.getConfig().getString("PARTY-EVENTS.TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return this.getConfig().getInteger("PARTY-EVENTS.SIZE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        String key = "PARTY-EVENTS.BUTTONS";
        if (plugin.getConfigHandler().isHCF_ENABLED()) {
            for ( PartyEventType type : PartyEventType.values() ) {
                buttons.put(this.getConfig().getInteger(key + "." + type.name() + ".SLOT"), new PartyEventButton(this.getConfig(), type));
            }
        } else {
            buttons.put(this.getConfig().getInteger(key + "PARTY_SPLIT.HCF_DISABLED_SLOT"), new PartyEventButton(this.getConfig(), PartyEventType.PARTY_SPLIT));
            buttons.put(this.getConfig().getInteger(key + "PARTY_FFA.HCF_DISABLED_SLOT"), new PartyEventButton(this.getConfig(), PartyEventType.PARTY_FFA));
        }
        return buttons;
    }
}
