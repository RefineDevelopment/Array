package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/15/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class PartyHCFButton extends Button {

    private final Array plugin;
    private final UUID uuid;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        Profile profile = plugin.getProfileManager().getProfile(uuid);
        Party party = plugin.getPartyManager().getPartyByUUID(uuid);
        String pvpClass = party.getKits().get(uuid);

        lore.add(CC.MENU_BAR);
        lore.add(pvpClass.equals("Diamond") ? "&7» &cDiamond" : "&7Diamond");
        lore.add(pvpClass.equals("Bard") ? "&7» &cBard" : "&7Bard");
        lore.add(pvpClass.equals("Archer") ? "&7» &cArcher" : "&7Archer");
        lore.add(pvpClass.equals("Rogue") ? "&7» &cRogue" : "&7Rogue");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(
                pvpClass.equals("Diamond") ? Material.DIAMOND_CHESTPLATE :
                pvpClass.equals("Bard") ? Material.GOLD_CHESTPLATE :
                pvpClass.equals("Archer") ? Material.LEATHER_CHESTPLATE :
                pvpClass.equals("Rogue") ? Material.IRON_CHESTPLATE : null)
                .name("&a" + profile.getName())
                .amount(1)
                .lore(lore)
                .build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getProfile(uuid);
        Party party = plugin.getPartyManager().getPartyByUUID(uuid);

        if (!party.isLeader(player.getUniqueId())) {
            Button.playFail(player);
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }

        Button.playSuccess(player);
        String pvpClass = party.getKits().get(uuid);

        if (pvpClass == null) {
            player.sendMessage(CC.translate("&cAn internal error occurred, please contact the author of this plugin!"));
            player.closeInventory();
            return;
        }

        switch (pvpClass) {
            case "Diamond": {
                party.getKits().replace(uuid, "Bard");
                party.broadcast(Locale.PARTY_HCF_UPDATED.toString()
                        .replace("<class>", "Bard")
                        .replace("<target>", profile.getName()));
                break;
            }
            case "Bard": {
                party.getKits().replace(uuid, "Archer");
                party.broadcast(Locale.PARTY_HCF_UPDATED.toString()
                        .replace("<class>", "Archer")
                        .replace("<target>", profile.getName()));
                break;
            }
            case "Archer": {
                party.getKits().replace(uuid, "Rogue");
                party.broadcast(Locale.PARTY_HCF_UPDATED.toString()
                        .replace("<class>", "Rogue")
                        .replace("<target>", profile.getName()));
                break;
            }
            case "Rogue": {
                party.getKits().replace(uuid, "Diamond");
                party.broadcast(Locale.PARTY_HCF_UPDATED.toString()
                        .replace("<class>", "Diamond")
                        .replace("<target>", profile.getName()));
                break;
            }
        }
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