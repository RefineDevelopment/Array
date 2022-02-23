package xyz.refinedev.practice.kit.kiteditor.menu;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.*;
import java.util.stream.Collectors;

public class KitEditorSelectKitMenu extends Menu {

    public KitEditorSelectKitMenu(Array plugin) {
        super(plugin);
    }

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Kit> kits = this.getPlugin().getKitManager().getKits().stream()
                .filter(Kit::isEnabled)
                .sorted(Comparator.comparing(k -> k.getGameRules().isEditable(), Comparator.reverseOrder()))
                .collect(Collectors.toList());

        for (Kit kit : kits) {
            buttons.put(buttons.size(), new KitDisplayButton(kit));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class KitDisplayButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(kit.getGameRules().isEditable() ? "&cClick to edit this kit." : "&cYou can not edit this kit.");
            return new ItemBuilder(kit.getDisplayIcon())
                    .name(kit.getDisplayName()).lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            if (!this.kit.getGameRules().isEditable()) {
                player.sendMessage(CC.translate("&cYou can not edit this kit"));
                return;
            }

            Profile profile = this.getPlugin().getProfileManager().getProfileByUUID(player.getUniqueId());
            profile.getKitEditor().setSelectedKit(kit);
            profile.getKitEditor().setPreviousState(profile.getState());

            new KitManagementMenu(this.getPlugin(), kit).openMenu(player);
        }

    }
}
