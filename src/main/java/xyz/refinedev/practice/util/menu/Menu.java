package xyz.refinedev.practice.util.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.custom.ButtonData;
import xyz.refinedev.practice.util.menu.custom.button.CustomButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public abstract class Menu {

    private final Array plugin = Array.getInstance();

    private final List<ButtonData> customButtons = new ArrayList<>();

    public static Map<String, Menu> currentlyOpenedMenus = new HashMap<>();
    private Map<Integer, Button> buttons = new HashMap<>();

    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private boolean placeholder = false;

    private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);

    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);
        ItemMeta itemMeta = item.getItemMeta();

        if (item.getType() != Material.SKULL_ITEM) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public void loadMenu(FoldersConfigurationFile config) {
        List<ButtonData> custom = plugin.getMenuManager().loadCustomButtons(config);
        if (custom != null && !custom.isEmpty()) {
            this.getCustomButtons().addAll(custom);
        }
        this.setPlaceholder(config.getBoolean("PLACEHOLDER"));

        if (this.isPlaceholder()) {
            ItemBuilder itemBuilder;
            try {
                Material material = Material.valueOf(config.getString("PLACEHOLDER_BUTTON.MATERIAL"));
                itemBuilder = new ItemBuilder(material);
                itemBuilder.name(config.getString("PLACEHOLDER_BUTTON.NAME"));
                if (config.getInteger("PLACEHOLDER_BUTTON.DATA") != 0)
                    itemBuilder.durability(config.getInteger("PLACEHOLDER_BUTTON.DATA"));
                itemBuilder.lore(config.getStringList("PLACEHOLDER_BUTTON.LORE"));
                itemBuilder.clearFlags();
            } catch (Exception e) {
                plugin.logger("Invalid Placeholder Button on " + config.getFile().getName().replace(".yml", "") + " Menu, turning off placeholder mode.");
                this.setPlaceholder(false);
                return;
            }

            this.setPlaceholderButton(Button.placeholder(itemBuilder.build()));
        }
    }

    /**
     * Open the menu for the Player
     *
     * @param player Player viewing the menu
     */
    public void openMenu(Player player) {
        this.buttons = this.getButtons(player);

        for ( ButtonData customButton : customButtons ) {
            if (customButton == null) return;
            this.buttons.put(customButton.getSlot(), new CustomButton(customButton));
        }

        Menu previousMenu = Menu.currentlyOpenedMenus.get(player.getName());
        Inventory inventory = null;
        int size = this.getSize() == -1 ? this.size(this.buttons) : this.getSize();
        boolean update = false;
        String title = CC.translate(this.getTitle(player));

        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (player.getOpenInventory() != null) {
            if (previousMenu == null) {
                player.closeInventory();
            } else {
                int previousSize = player.getOpenInventory().getTopInventory().getSize();

                if (previousSize == size && player.getOpenInventory().getTopInventory().getTitle().equals(title)) {
                    inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                } else {
                    previousMenu.setClosedByMenu(true);
                    player.closeInventory();
                }
            }
        }

        if (inventory == null) {
            inventory = Bukkit.createInventory(player, size, title);
        }

        inventory.setContents(new ItemStack[inventory.getSize()]);

        currentlyOpenedMenus.put(player.getName(), this);

        for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
            inventory.setItem(buttonEntry.getKey(), createItemStack(player, buttonEntry.getValue()));
        }

        if (this.isPlaceholder()) {
            for (int index = 0; index < size; index++) {
                if (this.buttons.get(index) == null) {
                    this.buttons.put(index, this.placeholderButton);
                    inventory.setItem(index, this.placeholderButton.getButtonItem(player));
                }
            }
        }

        if (update) {
            player.updateInventory();
        } else {
            player.openInventory(inventory);
        }

        this.onOpen(player);
        this.setClosedByMenu(false);
    }

    /**
     * Returns the size of buttons
     *
     * @param buttons {@link HashMap}
     * @return The amount of buttons
     */
    public int size(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    public int getSize() {
        return -1;
    }

    /**
     * Get slot at a particular x and y
     *
     * @return The slot
     */
    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    public abstract String getTitle(Player player);

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    public abstract Map<Integer, Button> getButtons(Player player);

    /**
     * This method runs when the menu is opened
     *
     * @param player {@link Player} player viewing the menu
     */
    public void onOpen(Player player) {
    }

    /**
     * This method runs when the menu is closed
     *
     * @param player {@link Player} player viewing the menu
     */
    public void onClose(Player player) {
    }

}
