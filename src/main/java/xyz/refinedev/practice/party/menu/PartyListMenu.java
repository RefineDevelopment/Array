package xyz.refinedev.practice.party.menu;

import lombok.AllArgsConstructor;
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
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.other.SkullCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyListMenu extends Menu {

    private final Array plugin = this.getPlugin();

    @Override
    public String getTitle(Player player) {
        return "&cClick to Manage a Member";
    }
    
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Party party = plugin.getPartyManager().getPartyByUUID(profile.getParty());
        party.getPlayers().forEach(pplayer -> buttons.put(buttons.size(), new PartyDisplayButton(pplayer)));
        return buttons;
    }

    @AllArgsConstructor
    public class PartyDisplayButton extends Button {

        private final Player pplayer;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore = new ArrayList<>();
            lore.add(CC.MENU_BAR);
            lore.add("&7Click here to manage");
            lore.add("&7" + pplayer.getName());
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(SkullCreator.itemFromUuid(pplayer.getUniqueId())).name("&a&l" + this.pplayer.getName()).lore(lore).durability(3).build();
        }
        
        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile senderProfile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            Profile receiverProfile = plugin.getProfileManager().getProfileByUUID(this.pplayer.getUniqueId());

            Party senderParty = plugin.getPartyManager().getPartyByUUID(senderProfile.getParty());
            Party receiverParty = plugin.getPartyManager().getPartyByUUID(receiverProfile.getParty());

            player.closeInventory();

            if (!senderParty.isLeader(player.getUniqueId())) {
                player.sendMessage(Locale.PARTY_NOTLEADER.toString());
                return;
            }
            if (receiverParty.isLeader(pplayer.getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot manage yourself.");
                return;
            }
            if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
                player.sendMessage(Locale.PARTY_PLAYER_LEFT.toString().replace("<leaver>", CC.translate("&7That player")));
                return;
            }
            new PartyMemberMenu(this.pplayer).openMenu(player);
        }
    }
}
