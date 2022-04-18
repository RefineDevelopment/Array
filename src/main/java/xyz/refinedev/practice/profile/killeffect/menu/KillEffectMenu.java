package xyz.refinedev.practice.profile.killeffect.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.menu.button.KillEffectButton;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/25/2022
 * Project: Array
 */

public class KillEffectMenu extends Menu {

    public KillEffectMenu() {
        this.setPlaceholder(true);
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return "&7Select a Kill Effect";
    }

    @Override
    public int getSize() {
        return 27;
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
        for ( int i = 10; i < 17; i++) {
            int position = i;
            Arrays.stream(KillEffect.values()).forEach(effect -> buttons.put(position, new KillEffectButton(effect)));
        }
        return buttons;
    }
}
