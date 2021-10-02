package xyz.refinedev.practice.kit.kiteditor.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitEditorSelectKitMenu extends Menu {

    private final Array plugin = this.getPlugin();

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Kit.getKits().forEach(kit -> {
            if (kit.isEnabled()) {
                if (kit.getGameRules().isEditable()) {
                    buttons.put(buttons.size(), new KitDisplayButton(kit));
                }
            }
        });

        return buttons;
    }

    @AllArgsConstructor
    private class KitDisplayButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&cClick to edit this kit.");
            return new ItemBuilder(kit.getDisplayIcon())
                    .name(kit.getDisplayName()).lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.getKitEditor().setSelectedKit(kit);
            profile.getKitEditor().setPreviousState(profile.getState());

            new KitManagementMenu(kit).openMenu(player);
        }

    }
}
