package me.array.ArrayPractice.party.menu;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class PartyListMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&bParty Members";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons =new HashMap<>();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getParty().getPlayers().forEach(pplayer -> buttons.put(buttons.size(), new PartyDisplayButton(pplayer)));
        return buttons;
    }
    
    public static class PartyDisplayButton extends Button
    {
        private final Player pplayer;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            final String lore = profile.getParty().isLeader(player.getUniqueId()) ? "&fClick to manage" : "";
            return new ItemBuilder(Material.SKULL_ITEM).name("&b" + this.pplayer.getName()).lore(lore).durability(3).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            final Profile senderProfile = Profile.getByUuid(player.getUniqueId());
            final Profile receiverProfile = Profile.getByUuid(this.pplayer.getUniqueId());
            if (!player.getUniqueId().equals(senderProfile.getParty().getLeader().getPlayer().getUniqueId())) {
                player.sendMessage(CC.RED + "You can only manage players as a leader.");
                return;
            }
            if (this.pplayer.getUniqueId().equals(receiverProfile.getParty().getLeader().getPlayer().getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot manage yourself.");
                return;
            }
            if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player is not in a party. (Left just now?)");
                return;
            }
            new ManagePartyMember(this.pplayer).openMenu(player);
        }
        
        @ConstructorProperties({ "pplayer" })
        public PartyDisplayButton(final Player pplayer) {
            this.pplayer = pplayer;
        }
    }
}
