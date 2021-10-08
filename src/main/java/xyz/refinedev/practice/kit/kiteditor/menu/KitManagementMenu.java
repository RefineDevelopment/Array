package xyz.refinedev.practice.kit.kiteditor.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
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

    private final Array plugin = this.getPlugin();
    private final Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");

    private final Kit kit;

    public KitManagementMenu(Kit kit) {
        this.kit = kit;
        this.setPlaceholder(true);
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return "&cManaging &7(" + kit.getName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        KitInventory[] kitInventories= profile.getStatisticsData().get(kit).getLoadouts();

        if (kitInventories == null) {
            return buttons;
        }

        int startPos = -1;

        for (int i = 0; i < 4; i++) {
            startPos += 2;

            KitInventory kitInventory= kitInventories[i];
            buttons.put(startPos, kitInventory == null ? new CreateKitButton(i) : new KitDisplayButton(kitInventory));
            buttons.put(startPos + 18, new LoadKitButton(i));
            buttons.put(startPos + 27, kitInventory == null ? PLACEHOLDER : new RenameKitButton(kitInventory));
            buttons.put(startPos + 36, kitInventory == null ? PLACEHOLDER : new DeleteKitButton(kitInventory));
        }

        buttons.put(36, new BackButton(new KitEditorSelectKitMenu()));

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!isClosedByMenu()) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.setState(profile.getKitEditor().getPreviousState());
            profile.getKitEditor().setSelectedKit(null);
        }
    }

    @AllArgsConstructor
    private class DeleteKitButton extends Button {

        private final KitInventory kitInventory;

        @Override
        public ItemStack getButtonItem(Player player) {
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
        public void clicked(Player player, ClickType clickType) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.getStatisticsData().get(kit).deleteKit(kitInventory);

            new KitManagementMenu(profile.getKitEditor().getSelectedKit()).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class CreateKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.IRON_SWORD)
                    .name("&a&lCreate Kit")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            Kit kit = profile.getKitEditor().getSelectedKit();

            if (kit == null) {
                player.closeInventory();
                return;
            }

            KitInventory kitInventory= new KitInventory(CC.RED + "Kit " + (index + 1));

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
            new KitEditorMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class RenameKitButton extends Button {

        private final KitInventory kitInventory;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SIGN)
                    .name("&a&lRename")
                    .lore(Arrays.asList(
                            "",
                            "&7Click to rename this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.getKitEditor().setActive(true);
            profile.getKitEditor().setRename(true);
            profile.getKitEditor().setSelectedKitInventory(kitInventory);

            player.closeInventory();
            player.sendMessage(Locale.KITEDITOR_RENAMING.toString().replace("<old_name>", kitInventory.getCustomName()));
        }

    }

    @AllArgsConstructor
    private class LoadKitButton extends Button {

        private final int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name("&a&lLoad/Edit")
                    .lore(Arrays.asList(
                            "",
                            "&7Click to edit this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

            if (profile.getKitEditor().getSelectedKit() == null) {
                player.closeInventory();
                return;
            }

            KitInventory kit = profile.getStatisticsData().get(profile.getKitEditor().getSelectedKit()).getLoadout(index);

            if (kit == null) {
                kit = new KitInventory(CC.RED + "Kit " + (index + 1));
                kit.setArmor(profile.getKitEditor().getSelectedKit().getKitInventory().getArmor());
                kit.setContents(profile.getKitEditor().getSelectedKit().getKitInventory().getContents());
                profile.getStatisticsData().get(profile.getKitEditor().getSelectedKit()).replaceKit(index, kit);
            }

            profile.getKitEditor().setSelectedKitInventory(kit);

            new KitEditorMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class KitDisplayButton extends Button {

        private final KitInventory kitInventory;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name("&c&l" + kitInventory.getCustomName())
                    .build();
        }

    }

}
