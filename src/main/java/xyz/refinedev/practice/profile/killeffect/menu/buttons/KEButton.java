package xyz.refinedev.practice.profile.killeffect.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/14/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class KEButton extends Button {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("profile_killeffects");
    private final KillEffect killEffect;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        ItemBuilder itemBuilder = new ItemBuilder(killEffect.getItemStack());
        List<String> lore = new ArrayList<>();

        String key;

        if (profile.isSelected(killEffect)) {
            key = "BUTTONS.SELECTED";
        } else if (killEffect.isPermissionEnabled() && !player.hasPermission(killEffect.getPermission())) {
            key = "BUTTONS.NO_PERM";
        } else {
            key = "BUTTONS.PERM";
        }

        for ( String string : config.getStringList(key)) {
            if (string.contains("<description>")) {
                lore.addAll(killEffect.getDescription());
                continue;
            }
            lore.add(string
                    .replace("<effect_animated_death>", killEffect.isAnimateDeath() ? "True" : "False")
                    .replace("<effect_lightning>", killEffect.isLightning() ? "True" : "False")
                    .replace("<effect_name>", killEffect.getEffect().name())
                    .replace("<effect_data>", String.valueOf(killEffect.getData()))
                    .replace("<effect_drops_clear>", killEffect.isDropsClear() ? "True" : "False"));
        }
        itemBuilder.name(config.getString(key + ".NAME").replace("<display_name>", killEffect.getName()));
        itemBuilder.lore(lore);

        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    public void clicked(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);

        if (profile.isSelected(killEffect)) {
            Button.playFail(player);
            player.sendMessage(Locale.KILL_EFFECT_ALREADY_SELECTED.toString());
            return;
        }

        if (killEffect.isPermissionEnabled() && !player.hasPermission(killEffect.getPermission())) {
            Button.playFail(player);
            player.sendMessage(Locale.KILL_EFFECT_NO_PERM.toString().replace("<store>", Array.getInstance().getConfigHandler().getSTORE()));
            return;
        }

        Button.playSuccess(player);
        profile.setKillEffect(killEffect.getUniqueId());
        player.sendMessage(Locale.KILL_EFFECT_SELECTED.toString().replace("<kill_effect>", killEffect.getName()));
    }

    /**
     * Should the click update the menu
     *
     * @param player The player clicking
     * @param clickType {@link ClickType}
     * @return {@link Boolean}
     */
    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
