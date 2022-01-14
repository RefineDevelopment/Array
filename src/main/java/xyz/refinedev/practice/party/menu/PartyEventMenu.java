package xyz.refinedev.practice.party.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.party.menu.buttons.PartyEventButton;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class PartyEventMenu extends Menu {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("party_events");

    public PartyEventMenu() {
        this.loadMenu(plugin, config);
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return config.getInteger("SIZE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        if (plugin.getConfigHandler().isHCF_ENABLED()) {
            for ( PartyEventType type : PartyEventType.values() ) {
                buttons.put(config.getInteger("BUTTONS." + type.name() + ".SLOT"), new PartyEventButton(config, type));
            }
        } else {
            buttons.put(config.getInteger("BUTTONS.PARTY_SPLIT.HCF_DISABLED_SLOT"), new PartyEventButton(config, PartyEventType.PARTY_SPLIT));
            buttons.put(config.getInteger("BUTTONS.PARTY_FFA.HCF_DISABLED_SLOT"), new PartyEventButton(config, PartyEventType.PARTY_FFA));
        }
        return buttons;
    }
}
