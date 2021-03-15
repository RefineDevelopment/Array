package me.drizzy.practice.party.menu;

import java.beans.ConstructorProperties;

import lombok.AllArgsConstructor;
import me.drizzy.practice.enums.PartyManageType;
import me.drizzy.practice.enums.PartyPrivacyType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import me.drizzy.practice.util.external.menu.Button;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.external.menu.Menu;

public class ManagePartySettings extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Party Settings";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        final List<Integer> occupied = new ArrayList<>();
        final int[] taken = {11,13,15};
        for ( int take : taken ) {
            occupied.add(take);
        }
        for ( int glassslots = 0; glassslots < 27; ++glassslots ) {
            if (!occupied.contains(glassslots)) {
                buttons.put(glassslots, new GlassButton());
            }
        }
        buttons.put(11, new SelectManageButton(PartyManageType.LIMIT));
        buttons.put(13, new SelectManageButton(PartyManageType.PUBLIC));
        buttons.put(15, new SelectManageButton(PartyManageType.MANAGE));
        return buttons;
    }
    
    private static class SelectManageButton extends Button
    {
        private final PartyManageType partyManageType;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            ArrayList<String> lore = new ArrayList<>();
            if (this.partyManageType == PartyManageType.LIMIT) {
                lore.add(CC.MENU_BAR);
                lore.add("&bCurrent Limit:");
                lore.add("&8 • &fLimit: &b" + profile.getParty().getLimit());
                lore.add("");
                lore.add("&8(&bLeft-Click&8) - &7Increase Limit");
                lore.add("&8(&bRight-Click&8) - &7Decrease Limit");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.INK_SACK).durability(2).name("&b" + this.partyManageType.getName()).lore(lore).build();
            }
            if (this.partyManageType == PartyManageType.PUBLIC) {
                lore.add(CC.MENU_BAR);
                lore.add("&bCurrent State:");
                lore.add("&8 • &fPublic: " + (profile.getParty().isPublic() ? "&aPublic" : "&eInvite Only"));
                lore.add("");
                lore.add("&bClick to change party state.");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.CHEST).name("&b" + this.partyManageType.getName()).lore(lore).build();
            }
            lore.add(CC.MENU_BAR);
            lore.add("&7Click here to manage your party");
            lore.add("&7members, you can make them either");
            lore.add("&7leader or kick them from the party");
            lore.add("");
            lore.add("&bClick to change party state.");
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(Material.SKULL_ITEM).name("&b" + this.partyManageType.getName()).lore(lore).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                return;
            }
            if (!player.hasPermission("array.donator")) {
                player.sendMessage(CC.translate("&7You do not have permission to use Party Settings."));
                player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.purgemc.club &7!"));
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                return;
            }
            if (this.partyManageType == PartyManageType.LIMIT) {
                if (clickType.isLeftClick()) {
                    if (profile.getParty().getLimit() < 100) {
                        profile.getParty().setLimit(profile.getParty().getLimit() + 1);
                        new ManagePartySettings().openMenu(player);
                    }
                }
                if (clickType.isRightClick()) {
                    if (profile.getParty().getLimit() > 1) {
                        profile.getParty().setLimit(profile.getParty().getLimit() - 1);
                        new ManagePartySettings().openMenu(player);
                    }
                }
            }
            else if (this.partyManageType == PartyManageType.PUBLIC) {
                if (!profile.getParty().isPublic()) {
                    profile.getParty().setPublic(true);
                    profile.getParty().setPrivacy(PartyPrivacyType.OPEN);
                }
                else {
                    profile.getParty().setPublic(false);
                    profile.getParty().setPrivacy(PartyPrivacyType.CLOSED);
                }
                new ManagePartySettings().openMenu(player);
            } else if (this.partyManageType == PartyManageType.MANAGE) {
                new PartyListMenu().openMenu(player);
            }
        }
        
        @ConstructorProperties({ "partyManageType" })
        public SelectManageButton(final PartyManageType partyManageType) {
            this.partyManageType=partyManageType;
        }
    }
    @AllArgsConstructor
    private static class GlassButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE).name("").durability(3).build();
        }
    }
}
