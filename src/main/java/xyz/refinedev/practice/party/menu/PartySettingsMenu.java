package xyz.refinedev.practice.party.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.enums.PartyManageType;
import xyz.refinedev.practice.party.menu.buttons.PartySettingsButton;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class PartySettingsMenu extends Menu {
    
    public PartySettingsMenu(Array plugin) {
        plugin.getMenuHandler().loadMenu(this, "PARTY-SETTINGS");

        this.setAutoUpdate(true);
        this.setUpdateAfterClick(true);
        this.setPlaceholder(true);
        this.setPlaceholderButton(Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 8, ""));
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return this.getConfig().getString("PARTY-SETTINGS.TITLE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for ( PartyManageType type : PartyManageType.values() ) {
            Button button = new PartySettingsButton(this.getConfig(), type);
            buttons.put(this.getConfig().getInteger("PARTY-SETTINGS.BUTTONS." + type.name() + ".SLOT"), button);
        }

        return buttons;
    }
}
