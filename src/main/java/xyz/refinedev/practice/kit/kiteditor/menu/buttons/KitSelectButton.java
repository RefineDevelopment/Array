package xyz.refinedev.practice.kit.kiteditor.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.kiteditor.KitEditor;
import xyz.refinedev.practice.kit.kiteditor.menu.KitManagementMenu;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.Arrays;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/25/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class KitSelectButton extends Button {

    private final Kit kit;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(kit.getDisplayIcon());
        itemBuilder.name(kit.getDisplayName());
        String text = kit.getGameRules().isEditable() ? "&cClick to edit this kit." : "&cYou can not edit this kit.";
        itemBuilder.lore(Arrays.asList("", text));
        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param plugin {@link org.bukkit.plugin.Plugin} Array
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        if (!this.kit.getGameRules().isEditable()) {
            player.sendMessage(CC.translate("&cYou can not edit this kit"));
            return;
        }

        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        KitEditor kitEditor = profile.getKitEditor();

        kitEditor.setSelectedKit(kit);
        kitEditor.setPreviousState(profile.getState());

        KitManagementMenu kitManagementMenu = new KitManagementMenu(kit);
        plugin.getMenuHandler().openMenu(kitManagementMenu, player);

        player.closeInventory();
    }
}
