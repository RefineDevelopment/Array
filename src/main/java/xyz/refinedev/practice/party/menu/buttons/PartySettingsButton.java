package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyManageType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

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

    private final Array plugin;
    private final FoldersConfigurationFile config;
    private final PartyManageType partyManageType;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        Party party = plugin.getPartyManager().getPartyByUUID(profile.getParty());

        String key = "BUTTONS." + partyManageType.name();

        Material material;
        try {
            material = Material.valueOf(config.getString(key + ".MATERIAL"));
        } catch (Exception e) {
            player.sendMessage(CC.translate("&cAn error occurred, please check console for details!"));
            plugin.consoleLog("&cPartySettingsMenu invalid Material Set for " + partyManageType.name() + " Button!");
            player.closeInventory();
            return null;
        }

        return new ItemBuilder(material)
                .name(config.getString(key + ".NAME"))
                .lore(config.getStringList(key + ".LORE").stream()
                        .map(string -> string
                                .replace("<party_limit>", String.valueOf(party.getLimit())
                                .replace("<party_state>", party.getPrivacy())))
                        .collect(Collectors.toList()))
                .clearFlags()
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
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (!profile.hasParty()) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            player.closeInventory();
            return;
        }

        Party party = plugin.getPartyManager().getPartyByUUID(profile.getParty());
        switch (partyManageType) {
            case LIMIT: {
                if (!player.hasPermission("array.party.limit")) {
                    Locale.PARTY_DONATOR.toList().forEach(player::sendMessage);
                    player.closeInventory();
                    return;
                }
                if (clickType.isLeftClick()) {
                    if (party.getLimit() < 100) {
                        party.setLimit(party.getLimit() + 1);
                    }
                }
                if (clickType.isRightClick()) {
                    if (party.getLimit() > 1) {
                        party.setLimit(party.getLimit() - 1);
                    }
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
