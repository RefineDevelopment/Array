package xyz.refinedev.practice.kit.kiteditor.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.kit.kiteditor.KitEditor;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.button.DisplayButton;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitEditorMenu extends Menu {

    private static final int[] ITEM_POSITIONS = new int[]{
            20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 47, 48, 49, 50, 51, 52,
            53
    };
    private static final int[] BORDER_POSITIONS = new int[]{1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 28, 37, 46};
    private static final Button BORDER_BUTTON = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 8, " ");
    

    public KitEditorMenu() {
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Array plugin, Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        return "&cEditing &7(" + profile.getKitEditor().getSelectedKit().getName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Kit kit = profile.getKitEditor().getSelectedKit();
        KitInventory kitInventory = profile.getKitEditor().getSelectedKitInventory();

        for (int border : BORDER_POSITIONS) {
            buttons.put(border, BORDER_BUTTON);
        }

        buttons.put(0, Button.placeholder(Material.NAME_TAG, (byte) 0, getTitle(plugin, player)));
        buttons.put(2, new SaveButton());
        buttons.put(6, new LoadDefaultKitButton());
        buttons.put(7, new ClearInventoryButton());
        buttons.put(8, new CancelButton());

        buttons.put(18, new ArmorDisplayButton(kitInventory.getArmor()[3]));
        buttons.put(27, new ArmorDisplayButton(kitInventory.getArmor()[2]));
        buttons.put(36, new ArmorDisplayButton(kitInventory.getArmor()[1]));
        buttons.put(45, new ArmorDisplayButton(kitInventory.getArmor()[0]));

        List<ItemStack> items = kit.getEditorItems();

        if (!kit.getEditorItems().isEmpty()) {
            for (int i = 20; i < (kit.getEditorItems().size() + 20); i++) {
                buttons.put(ITEM_POSITIONS[i - 20], new InfiniteItemButton(items.get(i - 20)));
            }
        }

        return buttons;
    }

    @Override
    public void onOpen(Array plugin, Player player) {
        if (this.isClosedByMenu()) return;
        
        PlayerUtil.reset(player);

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        KitEditor kitEditor = profile.getKitEditor();

        kitEditor.setActive(true);

        if (kitEditor.getSelectedKit() != null) {
            player.getInventory().setContents(kitEditor.getSelectedKitInventory().getContents());
        }
        player.updateInventory();
    }

    @Override
    public void onClose(Array plugin, Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        profile.getKitEditor().setActive(false);

        if (!profile.isInFight()) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> profileManager.refreshHotbar(profile));
        }

        this.setClosedByMenu(false);
    }

    @RequiredArgsConstructor
    private class ArmorDisplayButton extends Button {

        private final ItemStack itemStack;

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return new ItemStack(Material.AIR);
            }

            return new ItemBuilder(itemStack.clone())
                    .name(CC.RED + CraftItemStack.asNMSCopy(itemStack).getName())
                    .lore(CC.GREEN + "This is automatically equipped.")
                    .build();
        }

    }

    private class ClearInventoryButton extends Button {

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(13)
                    .name("&c&lClear Inventory")
                    .lore(Arrays.asList(
                            "&7This will clear your inventory",
                            "&7so you can start over."
                    ))
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            Button.playNeutral(player);
            player.getInventory().setContents(new ItemStack[36]);
            player.updateInventory();
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

    }

    private class LoadDefaultKitButton extends Button {

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(7)
                    .name(CC.RED + CC.BOLD + "&cLoad default kit")
                    .lore(Arrays.asList(
                            CC.RED + "&7Click this to init the default kit",
                            CC.RED + "&7into the kit editing menu."
                    ))
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            Button.playNeutral(player);

            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            KitInventory kitInventory = profile.getKitEditor().getSelectedKitInventory();

            player.setItemOnCursor(null);
            player.getInventory().setContents(kitInventory.getContents());
            player.updateInventory();
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

    }

    private class SaveButton extends Button {

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(5)
                    .name("&a&lSave")
                    .lore("&7Click this to save your kit.")
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            Button.playNeutral(player);
            player.closeInventory();

            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            KitEditor kitEditor = profile.getKitEditor();

            if (kitEditor.getSelectedKitInventory() != null) {
                kitEditor.getSelectedKitInventory().setContents(player.getInventory().getContents());
            }

            KitManagementMenu menu = new KitManagementMenu(kitEditor.getSelectedKit());
            
            profileManager.refreshHotbar(profile);
            plugin.getMenuHandler().openMenu(menu, player);
        }
    }

    private class CancelButton extends Button {

        @Override
        public ItemStack getButtonItem(Array plugin, Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(14)
                    .name("&c&lCancel")
                    .lore(Arrays.asList(
                            "&7Click this to abort editing your kit,",
                            "&7and return to the kit menu."
                    ))
                    .build();
        }

        @Override
        public void clicked(Array plugin, Player player, ClickType clickType) {
            Button.playNeutral(player);

            ProfileManager profileManager = plugin.getProfileManager();
            Profile profile = profileManager.getProfile(player.getUniqueId());
            KitEditor kitEditor = profile.getKitEditor();

            if (kitEditor.getSelectedKit() != null) {
                KitManagementMenu menu = new KitManagementMenu(kitEditor.getSelectedKit());
                plugin.getMenuHandler().openMenu(menu, player);
            }
        }

    }

    private class InfiniteItemButton extends DisplayButton {

        public InfiniteItemButton(ItemStack itemStack) {
            super(itemStack, false);
        }

        @Override
        public void clicked(Array plugin, Player player, int slot, ClickType clickType, int hotbar) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            ItemStack itemStack = inventory.getItem(slot);

            inventory.setItem(slot, itemStack);

            player.setItemOnCursor(itemStack);
            player.updateInventory();
        }

    }

}
