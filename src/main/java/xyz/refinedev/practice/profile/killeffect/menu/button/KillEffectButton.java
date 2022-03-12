package xyz.refinedev.practice.profile.killeffect.menu.button;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/25/2022
 * Project: Array
 */

public class KillEffectButton extends Button {

    private final KillEffect killEffect;

    public KillEffectButton(Array plugin, KillEffect killEffect) {
        super(plugin);

        this.killEffect = killEffect;
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    public ItemStack getButtonItem(Array plugin, Player player) {
        ProfileManager profileManager = this.getPlugin().getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        ItemBuilder itemBuilder = new ItemBuilder(killEffect.getIcon());
        itemBuilder.name(killEffect.getDisplayName());
        List<String> lore = new ArrayList<>();

        if (profile.getKillEffect().equals(killEffect)) {
            itemBuilder.enchantment(Enchantment.DAMAGE_UNDEAD, 1);
            itemBuilder.clearFlags();

            lore.add("&aYou have currently equipped this.");
            lore.add("");
            lore.add("&c[Click to un-equip]");
            itemBuilder.lore(lore);
            return itemBuilder.build();
        }

        lore.add("&7You don't have this equipped.");
        lore.add("");
        lore.add(player.hasPermission(killEffect.getPermission()) ? "&c[Click to equip]" : "&cYou don't have permission to equip!");

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
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = this.getPlugin().getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        if (profile.getKillEffect().equals(killEffect)) {
            Button.playSuccess(player);
            player.sendMessage(CC.translate("&7Your Kill Effect has been cleared!"));
            profile.setKillEffect(KillEffect.NONE);
            return;
        }

        if (!player.hasPermission(killEffect.getPermission())) {
            Button.playFail(player);
            player.sendMessage(CC.translate("&cYou don't have enough permissions to use this!"));
            return;
        }

        profile.setKillEffect(killEffect);
        Button.playSuccess(player);
        player.sendMessage(CC.translate("&7You have equipped the &c" + killEffect.getName() + " Kill Effect."));
    }

    /**
     * Should the click update the menu
     *
     * @param player The player clicking
     * @param clickType {@link ClickType}
     * @return {@link Boolean}
     */
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
