package me.array.ArrayPractice.party.menu;

import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.event.inventory.ClickType;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.beans.ConstructorProperties;
import me.array.ArrayPractice.party.PartyManage;
import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class ManagePartyMember extends Menu
{
    Player target;
    
    @Override
    public String getTitle(final Player player) {
        return "&bSelect an action for &9" + this.target.getName();
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        buttons.put(3, new SelectManageButton(PartyManage.LEADER));
        buttons.put(5, new SelectManageButton(PartyManage.KICK));
        return buttons;
    }
    
    @ConstructorProperties({ "target" })
    public ManagePartyMember(final Player target) {
        this.target = target;
    }
    
    private class SelectManageButton extends Button
    {
        private PartyManage partyManage;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder((this.partyManage == PartyManage.LEADER) ? Material.GOLD_SWORD : Material.REDSTONE).name("&9&l" + this.partyManage.getName()).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (this.partyManage == PartyManage.LEADER) {
                profile.getParty().leader(player, ManagePartyMember.this.target);
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
            }
            else {
                profile.getParty().leave(ManagePartyMember.this.target, true);
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
            }
        }
        
        @ConstructorProperties({ "partyManage" })
        public SelectManageButton(final PartyManage partyManage) {
            this.partyManage = partyManage;
        }
    }
}
