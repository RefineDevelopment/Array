package xyz.refinedev.practice.party.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyManageType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartySettingsMenu extends Menu {

    public PartySettingsMenu() {
        this.setAutoUpdate(true);
        this.setUpdateAfterClick(true);
        this.setPlaceholder(true);
        this.setPlaceholderButton(Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 8, ""));
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(final Player player) {
        return "&7Party Settings";
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new SelectManageButton(PartyManageType.LIMIT));
        buttons.put(13, new SelectManageButton(PartyManageType.PUBLIC));
        buttons.put(15, new SelectManageButton(PartyManageType.MANAGE));
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectManageButton extends Button {

        private final PartyManageType partyManageType;

        /**
         * Get itemStack of the Button
         *
         * @param player {@link Player} viewing the menu
         * @return {@link ItemStack}
         */
        @Override
        public ItemStack getButtonItem(final Player player) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            List<String> lore = new ArrayList<>();

            switch (partyManageType) {
                case LIMIT: {
                    lore.add(CC.MENU_BAR);
                    lore.add("&cCurrent Limit:");
                    lore.add("&8 • &fLimit: &c" + profile.getParty().getLimit());
                    lore.add("");
                    lore.add("&8(&cLeft-Click&8) - &7Increase Limit");
                    lore.add("&8(&cRight-Click&8) - &7Decrease Limit");
                    lore.add(CC.MENU_BAR);
                    return new ItemBuilder(Material.INK_SACK).durability(2).name("&c" + this.partyManageType.getName()).lore(lore).build();
                }
                case PUBLIC: {
                    lore.add(CC.MENU_BAR);
                    lore.add("&cCurrent State:");
                    lore.add("&8 • &fPublic: " + (profile.getParty().isPublic() ? "&aPublic" : "&eInvite Only"));
                    lore.add("");
                    lore.add("&cClick to change party state.");
                    lore.add(CC.MENU_BAR);
                    return new ItemBuilder(Material.CHEST).name("&c" + this.partyManageType.getName()).lore(lore).build();
                }
                default: {
                    lore.add(CC.MENU_BAR);
                    lore.add("&7Click here to manage your party");
                    lore.add("&7members, you can make them");
                    lore.add("&7leader or kick them from the party");
                    lore.add("");
                    lore.add("&cClick to change party state.");
                    lore.add(CC.MENU_BAR);
                    return new ItemBuilder(Material.SKULL_ITEM).name("&c" + this.partyManageType.getName()).lore(lore).build();
                }
            }
        }

        /**
         * This method is called upon clicking an
         * item on the menu
         *
         * @param player {@link Player} clicking
         * @param clickType {@link ClickType}
         */
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            Party party = profile.getParty();

            if (party == null) {
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
                case MANAGE: {
                    if (!player.hasPermission("array.party.manage")) {
                        Locale.PARTY_DONATOR.toList().forEach(player::sendMessage);
                        player.closeInventory();
                        return;
                    }
                    player.closeInventory();
                    new PartyListMenu().openMenu(player);
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
        public boolean shouldUpdate(final Player player, final ClickType clickType) {
            return true;
        }
    }
}
