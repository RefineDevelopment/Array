package xyz.refinedev.practice.kit.kiteditor.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.kit.kiteditor.KitEditor;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.button.BackButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KitManagementMenu extends Menu {

    private final Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");
    private final Kit kit;

    public KitManagementMenu(Kit kit) {
        this.kit = kit;

        this.setPlaceholder(true);
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Array plugin, Player player) {
        return "&cManaging &7(" + kit.getName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        KitInventory[] kitInventories = profile.getStatisticsData().get(kit).getKitInventories();

        if (kitInventories == null) return buttons;

        int startPos = -1;

        for (int i = 0; i < 4; i++) {
            startPos += 2;

            KitInventory kitInventory = kitInventories[i];
            buttons.put(startPos, kitInventory == null ? new CreateKitButton(i) : Button.placeholder(Material.BOOK, (byte) 0, kitInventory.getCustomName()));
            buttons.put(startPos + 18, new LoadKitButton(i));
            buttons.put(startPos + 27, kitInventory == null ? PLACEHOLDER : new RenameKitButton(kitInventory));
            buttons.put(startPos + 36, kitInventory == null ? PLACEHOLDER : new DeleteKitButton(kitInventory));
        }

        Menu menu = new KitEditorSelectKitMenu();
        buttons.put(36, new BackButton(menu));

        return buttons;
    }

    @Override
    public void onClose(Array plugin, Player player) {
        if (this.isClosedByMenu()) return;

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        profile.setState(profile.getKitEditor().getPreviousState());
        profile.getKitEditor().setSelectedKit(null);
    }

    @RequiredArgsConstructor
    private class DeleteKitButton extends Button {

        private final KitInventory kitInventory;

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .name("&c&lDelete")
                    .durability(14)
                    .lore(Arrays.asList(
                            "",
                            "&7Click to delete this kit.",
                            "&7You will &c&lNOT &7be able to",
                            "&7recover this kitInventory."
                    ))
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            KitEditor kitEditor = profile.getKitEditor();

            profile.getStatisticsData().get(kit).deleteKit(kitInventory);

            Menu menu = new KitManagementMenu(kitEditor.getSelectedKit());
            plugin.getMenuHandler().openMenu(menu, player);
        }

    }

    @RequiredArgsConstructor
    private class CreateKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.IRON_SWORD)
                    .name("&a&lCreate Kit")
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            Kit kit = profile.getKitEditor().getSelectedKit();

            if (kit == null) {
                player.closeInventory();
                return;
            }

            KitInventory kitInventory = new KitInventory(CC.RED + "Kit " + (index + 1));

            if (kit.getKitInventory() != null) {
                if (kit.getKitInventory().getArmor() != null) {
                    kitInventory.setArmor(kit.getKitInventory().getArmor());
                }

                if (kit.getKitInventory().getContents() != null) {
                    kitInventory.setContents(kit.getKitInventory().getContents());
                }
            }

            profile.getStatisticsData().get(kit).replaceKit(index, kitInventory);
            profile.getKitEditor().setSelectedKitInventory(kitInventory);

            Menu menu = new KitEditorMenu();
            plugin.getMenuHandler().openMenu(menu, player);
        }

    }

    @RequiredArgsConstructor
    private class RenameKitButton extends Button {

        private final KitInventory kitInventory;

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.SIGN)
                    .name("&a&lRename")
                    .lore(Arrays.asList(
                            "",
                            "&7Click to rename this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            setClosedByMenu(true);

            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            KitEditor kitEditor = profile.getKitEditor();

            kitEditor.setActive(true);
            kitEditor.setRename(true);
            kitEditor.setSelectedKitInventory(kitInventory);

            player.closeInventory();
            player.sendMessage(Locale.KITEDITOR_RENAMING.toString().replace("<old_name>", kitInventory.getCustomName()));
        }

    }

    @RequiredArgsConstructor
    private class LoadKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name("&a&lLoad/Edit")
                    .lore(Arrays.asList(
                            "",
                            "&7Click to edit this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            KitEditor kitEditor = profile.getKitEditor();

            if (kitEditor.getSelectedKit() == null) {
                player.closeInventory();
                return;
            }

            KitInventory kit = profile.getStatisticsData().get(kitEditor.getSelectedKit()).getLoadout(index);

            if (kit == null) {
                kit = new KitInventory(CC.RED + "Kit " + (index + 1));
                kit.setArmor(profile.getKitEditor().getSelectedKit().getKitInventory().getArmor());
                kit.setContents(profile.getKitEditor().getSelectedKit().getKitInventory().getContents());
                profile.getStatisticsData().get(kitEditor.getSelectedKit()).replaceKit(index, kit);
            }

            kitEditor.setSelectedKitInventory(kit);

            Menu menu = new KitEditorMenu();
            plugin.getMenuHandler().openMenu(menu, player);
        }

    }
}
