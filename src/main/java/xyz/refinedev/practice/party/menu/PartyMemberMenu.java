package xyz.refinedev.practice.party.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
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

@RequiredArgsConstructor
public class PartyMemberMenu extends Menu {

    private final Array plugin = this.getPlugin();
    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&cSelect an action for &9" + this.target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(2, new SelectManageButton(target, PartyManageType.LEADER));
        buttons.put(4, new SelectManageButton(target, PartyManageType.KICK));
        buttons.put(6, new SelectManageButton(target, PartyManageType.BAN));
        return buttons;
    }

    @RequiredArgsConstructor
    private class SelectManageButton extends Button {

        private final Player target;
        private final PartyManageType partyManageType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            if (this.partyManageType == PartyManageType.LEADER) {
                lore.add(CC.MENU_BAR);
                lore.add("&7Click here to make &c" + target.getName());
                lore.add("&7the party leader, this will grant them");
                lore.add("&c&lcomplete &7control of the party!");
                lore.add("");
                lore.add("&cClick to Grant party leadership.");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.GOLD_SWORD).name("&c" + this.partyManageType.getName()).lore(lore).build();
            }
            if (this.partyManageType == PartyManageType.KICK) {
                lore.add(CC.MENU_BAR);
                lore.add("&7Click here to Kick &c" + target.getName());
                lore.add("&7this will make them leave the party");
                lore.add("&7but, they can join back unless invited");
                lore.add("");
                lore.add("&cClick to Kick " + target.getName() + ".");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.BOOK).name("&c" + this.partyManageType.getName()).lore(lore).build();
            }
            lore.add(CC.MENU_BAR);
            lore.add("&7Click here to Ban &c" + target.getName());
            lore.add("&7this will make them leave the party");
            lore.add("&7and they will not be able to join back");
            lore.add("&7unless unbanned manually!");
            lore.add("");
            lore.add("&cClick to Ban " + target.getName() + ".");
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(Material.SKULL_ITEM).name("&c" + this.partyManageType.getName()).lore(lore).build();
        }
        
        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            if (!profile.hasParty()) {
                player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
                return;
            }
            player.closeInventory();

            Party party = plugin.getPartyManager().getPartyByUUID(profile.getParty());
            switch (this.partyManageType) {
                case LEADER: {
                    plugin.getPartyManager().leader(this.target, party);
                    break;
                }
                case KICK: {
                    plugin.getPartyManager().kick(this.target, party);
                    break;
                }
                case BAN: {
                    plugin.getPartyManager().ban(this.target, party);
                    plugin.getPartyManager().kick(this.target, party);
                    break;
                }
            }
        }
    }
}
