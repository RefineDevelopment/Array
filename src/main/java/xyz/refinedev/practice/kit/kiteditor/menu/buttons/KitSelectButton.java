package xyz.refinedev.practice.kit.kiteditor.menu.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.kiteditor.menu.KitManagementMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/25/2022
 * Project: Array
 */

public class KitSelectButton extends Button {

    private final Kit kit;

    public KitSelectButton(Array plugin, Kit kit) {
        super(plugin);

        this.kit = kit;
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(kit.getDisplayIcon());
        itemBuilder.name(kit.getDisplayName());
        String text = kit.getGameRules().isEditable() ? "&cClick to edit this kit." : "&cYou can not edit this kit.";
        itemBuilder.lore(Arrays.asList("", text));
        return itemBuilder.build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (!this.kit.getGameRules().isEditable()) {
            player.sendMessage(CC.translate("&cYou can not edit this kit"));
            return;
        }

        Profile profile = this.getPlugin().getProfileManager().getProfile(player.getUniqueId());
        profile.getKitEditor().setSelectedKit(kit);
        profile.getKitEditor().setPreviousState(profile.getState());

        KitManagementMenu kitManagementMenu = new KitManagementMenu(this.getPlugin(), kit);
        kitManagementMenu.openMenu(player);

        player.closeInventory();
    }
}
