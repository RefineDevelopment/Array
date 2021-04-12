package me.drizzy.practice.party.menu;

import me.drizzy.practice.enums.PartyManageType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.profile.Profile;
import org.bukkit.event.inventory.ClickType;
import me.drizzy.practice.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.beans.ConstructorProperties;

import java.util.ArrayList;
import java.util.HashMap;
import me.drizzy.practice.util.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.menu.Menu;

public class ManagePartyMember extends Menu {
    Player target;

    @Override
    public String getTitle(final Player player) {
        return "&bSelect an action for &9" + this.target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons=new HashMap<>();
        buttons.put(2, new SelectManageButton(PartyManageType.LEADER));
        buttons.put(4, new SelectManageButton(PartyManageType.KICK));
        buttons.put(6, new SelectManageButton(PartyManageType.BAN));
        return buttons;
    }

    @ConstructorProperties({"target"})
    public ManagePartyMember(final Player target) {
        this.target=target;
    }

    private class SelectManageButton extends Button {
        private final PartyManageType partyManageType;

        @Override
        public ItemStack getButtonItem(final Player player) {
            ArrayList<String> lore=new ArrayList<>();
            Player target = ManagePartyMember.this.target;
            if (this.partyManageType == PartyManageType.LEADER) {
                lore.add(CC.MENU_BAR);
                lore.add("&7Click here to make &b" + target.getName());
                lore.add("&7the party leader, this will grant them");
                lore.add("&b&lCOMPLETE &7control of the party!");
                lore.add("");
                lore.add("&bClick to Grant party leadership.");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.GOLD_SWORD).name("&b" + this.partyManageType.getName()).lore(lore).build();
            }
            if (this.partyManageType == PartyManageType.KICK) {
                lore.add(CC.MENU_BAR);
                lore.add("&7Click here to Kick &b" + target.getName());
                lore.add("&7this will make them leave the party");
                lore.add("&7but, they can join back unless invited");
                lore.add("");
                lore.add("&bClick to Kick " + target.getName() + ".");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.BOOK).name("&b" + this.partyManageType.getName()).lore(lore).build();
            }
            lore.add(CC.MENU_BAR);
            lore.add("&7Click here to Ban &b" + target.getName());
            lore.add("&7this will make them leave the party");
            lore.add("&7and they will not be able to join back");
            lore.add("&7unless unbanned manually!");
            lore.add("");
            lore.add("&bClick to Ban " + target.getName() + ".");
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(Material.SKULL_ITEM).name("&b" + this.partyManageType.getName()).lore(lore).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (this.partyManageType == PartyManageType.LEADER) {
                profile.getParty().leader(player, ManagePartyMember.this.target);
            }
            else if (this.partyManageType == PartyManageType.MANAGE) {
                profile.getParty().leave(ManagePartyMember.this.target, true);
            }
            else if (this.partyManageType == PartyManageType.BAN) {
                profile.getParty().leave(ManagePartyMember.this.target, true);
                profile.getParty().ban(ManagePartyMember.this.target);
            }
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
        }
        
        @ConstructorProperties({ "partyManageType" })
        public SelectManageButton(final PartyManageType partyManageType) {
            this.partyManageType=partyManageType;
        }
    }
}
