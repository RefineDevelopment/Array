package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.PartyManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyManageType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.ButtonUtil;

import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/14/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class PartySettingsButton extends Button {

    private final BasicConfigurationFile config;
    private final PartyManageType partyManageType;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        PartyManager partyManager = plugin.getPartyManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());

        String key = "BUTTONS." + partyManageType.name();
        Material material = ButtonUtil.getMaterial(config, key);

        ItemBuilder itemBuilder = new ItemBuilder(material);
        itemBuilder.name(config.getString(key + ".NAME"));
        itemBuilder.lore(config.getStringList(key + ".LORE")
                .stream()
                .map(string -> string
                        .replace("<party_limit>", String.valueOf(party.getLimit())
                        .replace("<party_state>", party.getPrivacy())))
                .collect(Collectors.toList()));
        itemBuilder.clearFlags();
        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = plugin.getProfileManager();
        PartyManager partyManager = plugin.getPartyManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());

        if (!profile.hasParty()) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            player.closeInventory();
            return;
        }

        switch (partyManageType) {
            case LIMIT: {
                if (!player.hasPermission("array.party.limit")) {
                    Locale.PARTY_DONATOR.toList().forEach(player::sendMessage);
                    player.closeInventory();
                    return;
                }
                if (clickType.isLeftClick()) {
                    if (party.getLimit() < 100) party.setLimit(party.getLimit() + 1);
                }
                if (clickType.isRightClick()) {
                    if (party.getLimit() > 1) party.setLimit(party.getLimit() - 1);
                }
                break;
            }
            case PUBLIC: {
                if (!player.hasPermission("array.party.privacy")) {
                    Locale.PARTY_DONATOR.toList().forEach(player::sendMessage);
                    player.closeInventory();
                    return;
                }

                party.setPublic(!party.isPublic());
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
