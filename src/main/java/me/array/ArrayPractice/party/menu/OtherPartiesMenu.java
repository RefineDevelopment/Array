package me.array.ArrayPractice.party.menu;

import java.beans.ConstructorProperties;

import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.event.inventory.ClickType;
import java.util.List;
import org.bukkit.Material;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.pagination.PaginatedMenu;

public class OtherPartiesMenu extends PaginatedMenu
{
    @Override
    public String getPrePaginatedTitle(final Player player) {
        return "&bOther Parties";
    }
    
    @Override
    public Map<Integer, Button> getAllPagesButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final Map<Integer, OtherPartyButton> map;
        final OtherPartyButton otherPartyButton;
        Party.getParties().forEach(party -> buttons.put(buttons.size(), new OtherPartyButton(party)));
        return buttons;
    }

    public static class OtherPartyButton extends Button
    {
        private Party party;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore = new ArrayList<String>();
            int added = 0;
            for (final TeamPlayer teamPlayer : this.party.getTeamPlayers()) {
                if (added >= 10) {
                    break;
                }
                if (teamPlayer.getPlayer() == null) {
                    continue;
                }
                lore.add(CC.GRAY + " * " + CC.RESET + teamPlayer.getPlayer().getName());
                ++added;
            }
            if (this.party.getTeamPlayers().size() != added) {
                lore.add(CC.GRAY + " and " + (this.party.getTeamPlayers().size() - added) + " others...");
            }
            return new ItemBuilder(Material.SKULL_ITEM).name("&bParty of " + this.party.getLeader().getPlayer().getName()).amount(this.party.getTeamPlayers().size()).durability(3).lore(lore).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            final Profile senderProfile = Profile.getByUuid(player.getUniqueId());
            final Profile receiverProfile = Profile.getByUuid(this.party.getLeader().getPlayer().getUniqueId());
            if (!player.getUniqueId().equals(senderProfile.getParty().getLeader().getPlayer().getUniqueId())) {
                player.sendMessage(CC.RED + "You can only duel parties as a leader.");
                return;
            }
            if (senderProfile.isBusy(player)) {
                player.sendMessage(CC.RED + "You cannot duel right now.");
                return;
            }
            if (receiverProfile.isBusy(receiverProfile.getParty().getLeader().getPlayer())) {
                player.sendMessage(CC.translate(this.party.getLeader().getPlayer().getDisplayName()) + CC.RED + " is currently busy.");
                return;
            }
            if (!receiverProfile.getOptions().isReceiveDuelRequests()) {
                player.sendMessage(CC.RED + "That player is not accepting duel requests at the moment.");
                return;
            }
            if (!senderProfile.canSendDuelRequest(player)) {
                player.sendMessage(CC.RED + "You have already sent that player a duel request.");
                return;
            }
            if (senderProfile.getParty() != null && receiverProfile.getParty() == null) {
                player.sendMessage(CC.RED + "That player is not in a party.");
                return;
            }
            if (player.getUniqueId().equals(receiverProfile.getParty().getLeader().getPlayer().getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot duel yourself.");
                return;
            }
            new OtherPartiesSelectEventMenu(player, this.party.getLeader().getPlayer(), this.party).openMenu(player);
        }
        
        @ConstructorProperties({ "party" })
        public OtherPartyButton(final Party party) {
            this.party = party;
        }
    }
}
