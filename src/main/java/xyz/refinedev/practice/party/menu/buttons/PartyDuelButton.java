package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.PartyManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.SkullCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/29/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class PartyDuelButton extends Button {

    private final Party party;

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        boolean fighting = plugin.getPartyManager().isFighting(party.getUniqueId());

        ItemStack itemStack = SkullCreator.itemFromUuid(party.getLeader().getUniqueId());
        ItemBuilder builder = new ItemBuilder(fighting ? new ItemStack(Material.SKULL) : itemStack);

        builder.name(ChatColor.RED + party.getName());
        //If they are in a fight, then it displays their skull
        //as a wither skull
        if (fighting) builder.durability(1);

        List<String> lore = new ArrayList<>();
        lore.add("");
        for ( TeamPlayer member : party.getTeamPlayers()) {
            ChatColor color = party.isLeader(member.getUniqueId()) ? ChatColor.RED : ChatColor.WHITE;
            lore.add(color + member.getUsername());
        }
        lore.add("");
        builder.lore(lore);
        builder.clearFlags();

        return builder.build();
    }

    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = plugin.getProfileManager();
        PartyManager partyManager = plugin.getPartyManager();

        Profile senderProfile = profileManager.getProfile(player.getUniqueId());
        Profile receiverProfile = profileManager.getProfile(this.party.getLeader().getUniqueId());

        Party senderParty = partyManager.getPartyByUUID(senderProfile.getParty());
        Party receiverParty = partyManager.getPartyByUUID(receiverProfile.getParty());

        player.closeInventory();

        if (!senderParty.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profileManager.isPendingDuelRequest(senderProfile, player)) {
            player.sendMessage(Locale.DUEL_ALREADYSENT.toString());
            return;
        }
        if (receiverParty == null) {
            player.sendMessage(Locale.DUEL_DISBANDED.toString());
            return;
        }

        player.chat("/duel " + party.getLeader().getUsername());
    }
}