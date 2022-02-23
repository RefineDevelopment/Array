package xyz.refinedev.practice.party.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.enums.PartyManageType;
import xyz.refinedev.practice.party.menu.buttons.PartySettingsButton;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

//TODO: config
public class PartySettingsMenu extends Menu {

    private final FoldersConfigurationFile config;

    public PartySettingsMenu(Array plugin) {
        super(plugin);
        this.config = plugin.getMenuHandler().getConfigByName("party_settings");

        this.loadMenu(config);

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
    public String getTitle(Player player) {
        return config.getString("TITLE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for ( PartyManageType type : PartyManageType.values() ) {
            Button button = new PartySettingsButton(this.getPlugin(), config, type);
            buttons.put(config.getInteger("BUTTONS." + type.name() + ".SLOT"), button);
        }

        return buttons;
    }
}
