package xyz.refinedev.practice.util.menu;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.refinedev.practice.util.other.StringUtils;

@Getter
public abstract class Button {

    /**
     * Create a quick and easy placeholder Button
     */
    public static Button placeholder(Material material, byte data, String... title) {
        return (new Button() {
            public ItemStack getButtonItem(Player player) {
                ItemStack it = new ItemStack(material, 1, data);
                ItemMeta meta = it.getItemMeta();

                meta.setDisplayName(StringUtils.join(title));
                it.setItemMeta(meta);

                return it;
            }
        });
    }

    public static Button placeholder(ItemStack itemStack) {
        return (new Button() {
            public ItemStack getButtonItem(Player player) {
                return itemStack;
            }
        });
    }

    /**
     * Play a fail sound upon clicking
     *
     * @param player {@link Player} hearing the sound
     */
    public static void playFail(Player player) {
        player.playSound(player.getLocation(), Sound.DIG_GRASS, 20F, 0.1F);

    }

    /**
     * Play a successful sound upon clicking
     *
     * @param player {@link Player} hearing the sound
     */
    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20F, 15F);
    }

    /**
     * Play a neutral sound upon clicking
     *
     * @param player {@link Player} hearing the sound
     */
    public static void playNeutral(Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 20F, 1F);
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    public abstract ItemStack getButtonItem(Player player);

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    public void clicked(Player player, ClickType clickType) {
    }

    /**
     * This method is called when the player clicks
     * on an item of a certain slot
     *
     * @param player {@link Player} clicking
     * @param slot {@link Integer} slot
     * @param clickType {@link ClickType} clickType
     * @param hotbarSlot the hotbar slot of the player
     */
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
    }

    /**
     * Should the click cancel the event and do nothing
     *
     * @param player The player clicking
     * @param clickType {@link ClickType}
     * @return {@link Boolean}
     */
    public boolean shouldCancel(Player player, ClickType clickType) {
        return true;
    }

    /**
     * Should the click update the menu
     *
     * @param player The player clicking
     * @param clickType {@link ClickType}
     * @return {@link Boolean}
     */
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }

}