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
        return "&7Party List";
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
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(SkullCreator.itemFromUuid(pplayer.getUniqueId()))
                    .name("&a" + this.pplayer.getName())
                    .durability(3)
                    .build();
        }
    }
}
