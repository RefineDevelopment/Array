package xyz.refinedev.practice.profile.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/3/2021
 * Project: Array
 */

public class KillEffectsMenu extends PaginatedMenu {

    {setPlaceholder(true);}

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("&7Select a Kill Effect");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( KillEffect killEffect : Array.getInstance().getKillEffectManager().getKillEffects() ) {
            buttons.put(buttons.size(), new KillButton(killEffect));
        }
        return buttons;
    }

    @RequiredArgsConstructor
    private static class KillButton extends Button {

        private final KillEffect killEffect;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER).lore(killEffect.getDescription()).name(killEffect.getDisplayName()).clearFlags().build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByPlayer(player);

            if (killEffect.isPermissionEnabled() && !player.hasPermission("array." + killEffect.getPermission())) {
                player.sendMessage("&7You don't have permission to equip this!");
                return;
            }

            profile.setKillEffect(killEffect);
            TaskUtil.run(profile::save);
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }
    }
}
