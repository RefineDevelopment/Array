package me.drizzy.practice.kiteditor.menu;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.BukkitReflection;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import me.drizzy.practice.util.external.menu.button.DisplayButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

    KitEditorMenu() {
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        return "&bEditing &7(" + profile.getKitEditor().getSelectedKit().getName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int border : BORDER_POSITIONS) {
            buttons.put(border, BORDER_BUTTON);
        }

        buttons.put(0, new CurrentKitButton());
        buttons.put(2, new SaveButton());
        buttons.put(6, new LoadDefaultKitButton());
        buttons.put(7, new ClearInventoryButton());
        buttons.put(8, new CancelButton());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        Kit kit = profile.getKitEditor().getSelectedKit();
        KitInventory kitInventory= profile.getKitEditor().getSelectedKitInventory();

        buttons.put(18, new ArmorDisplayButton(kitInventory.getArmor()[3]));
        buttons.put(27, new ArmorDisplayButton(kitInventory.getArmor()[2]));
        buttons.put(36, new ArmorDisplayButton(kitInventory.getArmor()[1]));
        buttons.put(45, new ArmorDisplayButton(kitInventory.getArmor()[0]));

        List<ItemStack> items = kit.getEditRules().getEditorItems();

        if (!kit.getEditRules().getEditorItems().isEmpty()) {
            for (int i = 20; i < (kit.getEditRules().getEditorItems().size() + 20); i++) {
                buttons.put(ITEM_POSITIONS[i - 20], new InfiniteItemButton(items.get(i - 20)));
            }
        }

        return buttons;
    }

    @Override
    public void onOpen(Player player) {
        if (!isClosedByMenu()) {
            PlayerUtil.reset(player);

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getKitEditor().setActive(true);

            if (profile.getKitEditor().getSelectedKit() != null) {
                player.getInventory().setContents(profile.getKitEditor().getSelectedKitInventory().getContents());
            }

            player.updateInventory();
        }
    }

    @Override
    public void onClose(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getKitEditor().setActive(false);

        if (!profile.isInFight()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                PlayerUtil.reset(player, false);
                profile.refreshHotbar();
                }
            }.runTask(Array.getInstance());
        }
    }

    @AllArgsConstructor
    private static class ArmorDisplayButton extends Button {

        private final ItemStack itemStack;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return new ItemStack(Material.AIR);
            }

            return new ItemBuilder(itemStack.clone())
                    .name(CC.AQUA + BukkitReflection.getItemStackName(itemStack))
                    .lore(CC.GREEN + "This is automatically equipped.")
                    .build();
        }

    }

    @AllArgsConstructor
    private static class CurrentKitButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            return new ItemBuilder(Material.NAME_TAG)
                    .name("&bEditing &r" + profile.getKitEditor().getSelectedKit().getName())
                    .build();
        }

    }

    @AllArgsConstructor
    private static class ClearInventoryButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(13)
                    .name("&b&lClear Inventory")
                    .lore(Arrays.asList(
                            "&7This will clear your inventory",
                            "&7so you can start over."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);
            player.getInventory().setContents(new ItemStack[36]);
            player.updateInventory();
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

    }

    @AllArgsConstructor
    private static class LoadDefaultKitButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(7)
                    .name(CC.RED + CC.BOLD + "&bLoad default kit")
                    .lore(Arrays.asList(
                            CC.RED + "&7Click this to load the default kit",
                            CC.RED + "&7into the kit editing menu."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);

            Profile profile = Profile.getByUuid(player.getUniqueId());

            player.getInventory()
                    .setContents(profile.getKitEditor().getSelectedKit().getKitInventory().getContents());
            player.updateInventory();
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

    }

    @AllArgsConstructor
    private static class SaveButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(5)
                    .name("&a&lSave")
                    .lore("&7Click this to save your kit.")
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);
            player.closeInventory();

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getKitEditor().getSelectedKitInventory() != null) {
                profile.getKitEditor().getSelectedKitInventory().setContents(player.getInventory().getContents());
            }

        PlayerUtil.reset(player, false);
        profile.refreshHotbar();

            new KitManagementMenu(profile.getKitEditor().getSelectedKit()).openMenu(player);
        }

    }

    @AllArgsConstructor
    private static class CancelButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(14)
                    .name("&b&lCancel")
                    .lore(Arrays.asList(
                            "&7Click this to abort editing your kit,",
                            "&7and return to the kit menu."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getKitEditor().getSelectedKit() != null) {
                new KitManagementMenu(profile.getKitEditor().getSelectedKit()).openMenu(player);
            }
        }

    }

    private static class InfiniteItemButton extends DisplayButton {

        InfiniteItemButton(ItemStack itemStack) {
            super(itemStack, false);
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbar) {
            Inventory inventory=player.getOpenInventory().getTopInventory();
            ItemStack itemStack=inventory.getItem(slot);

            inventory.setItem(slot, itemStack);

            player.setItemOnCursor(itemStack);
            player.updateInventory();
        }

    }

}
