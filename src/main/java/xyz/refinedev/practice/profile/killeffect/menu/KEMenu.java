package xyz.refinedev.practice.profile.killeffect.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.menu.buttons.KEButton;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/14/2021
 * Project: Array
 */

public class KEMenu extends Menu {

    private final FoldersConfigurationFile config;

    public KEMenu(Array plugin) {
        super(plugin);
        this.config = plugin.getMenuHandler().getConfigByName("profile_killeffects");
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
     * Get the size of the Menu's Inventory
     *
     * @return {@link Integer} size
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
        List<KillEffect> killEffects = new ArrayList<>(this.getPlugin().getKillEffectManager().getKillEffects());
        killEffects.sort(Comparator.comparing(KillEffect::getPriority));

        for ( KillEffect killEffect : killEffects) {
            buttons.put(buttons.size(), new KEButton(this.getPlugin(), killEffect));
        }
        return buttons;
    }
}
