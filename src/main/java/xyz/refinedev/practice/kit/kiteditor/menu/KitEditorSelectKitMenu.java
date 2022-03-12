package xyz.refinedev.practice.kit.kiteditor.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.kiteditor.menu.buttons.KitSelectButton;
import xyz.refinedev.practice.managers.KitManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.*;
import java.util.stream.Collectors;

public class KitEditorSelectKitMenu extends Menu {

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public String getTitle(Array plugin, Player player) {
        return "&7Select a kit";
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
        KitManager kitManager = plugin.getKitManager();
        List<Kit> kits = kitManager.getKits().stream()
                .filter(Kit::isEnabled)
                .sorted(Comparator.comparing(k -> k.getGameRules().isEditable(), Comparator.reverseOrder()))
                .collect(Collectors.toList());

        for (Kit kit : kits) {
            buttons.put(buttons.size(), new KitSelectButton(kit));
        }

        return buttons;
    }
}
