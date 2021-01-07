package me.array.ArrayPractice.party.menu;

import java.beans.ConstructorProperties;

import me.array.ArrayPractice.party.PartyManage;
import me.array.ArrayPractice.party.PartyPrivacy;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class ManagePartySettings extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Party Settings";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(2, new SelectManageButton(PartyManage.LIMIT));
        buttons.put(4, new SelectManageButton(PartyManage.PUBLIC));
        buttons.put(6, new SelectManageButton(PartyManage.MANAGE_MEMBERS));
        return buttons;
    }
    
    private static class SelectManageButton extends Button
    {
        private final PartyManage partyManage;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (this.partyManage == PartyManage.LIMIT) {
                return new ItemBuilder(Material.REDSTONE_TORCH_ON).name("&b" + this.partyManage.getName()).lore("&7Limit: " + profile.getParty().getLimit()).build();
            }
            if (this.partyManage == PartyManage.PUBLIC) {
                return new ItemBuilder(Material.CHEST).name("&b" + this.partyManage.getName()).lore("&7Public: " + profile.getParty().isPublic()).build();
            }
            return new ItemBuilder(Material.SKULL_ITEM).name("&b" + this.partyManage.getName()).lore("&7Click to Manage Members").build();
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
            if (!player.hasPermission("practice.donator")) {
                player.sendMessage(CC.RED + "You need a Donator Rank for this");
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                return;
            }
            if (this.partyManage == PartyManage.LIMIT) {
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
            else if (this.partyManage == PartyManage.PUBLIC) {
                if (!profile.getParty().isPublic()) {
                    profile.getParty().setPublic(true);
                    profile.getParty().setPrivacy(PartyPrivacy.OPEN);
                }
                else {
                    profile.getParty().setPublic(false);
                    profile.getParty().setPrivacy(PartyPrivacy.CLOSED);
                }
                new ManagePartySettings().openMenu(player);
            } else if (this.partyManage == PartyManage.MANAGE_MEMBERS) {
                new PartyListMenu().openMenu(player);
            }
        }
        
        @ConstructorProperties({ "partyManage" })
        public SelectManageButton(final PartyManage partyManage) {
            this.partyManage = partyManage;
        }
    }
}
