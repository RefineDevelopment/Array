package xyz.refinedev.practice.party.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/27/2021
 * Project: Array
 */

public class PartyClassSelectMenu extends PaginatedMenu {

    {this.setAutoUpdate(true);}

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Select Armor Class";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        //Maybe they left?, shouldn't happen but just checking
        for (UUID uuid : new ArrayList<>(party.getKits().keySet())) {
            if (!party.getPlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()).contains(uuid)) {
                party.getKits().remove(uuid);
            }
        }

        if (!party.getPlayers().isEmpty()) {
            party.getPlayers().forEach(target -> buttons.put(buttons.size(), new MemberDisplayButton(target.getUniqueId())));
        }
        return buttons;
    }

    @RequiredArgsConstructor
    public static class MemberDisplayButton extends Button {

        private final UUID uuid;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            
            Profile profile = Profile.getByUuid(uuid);
            Party party = profile.getParty();
            String pvpClass = party.getKits().get(uuid);

            lore.add(CC.MENU_BAR);
            lore.add(pvpClass.equals("Diamond") ? "&7» &cDiamond" : "&7Diamond");
            lore.add(pvpClass.equals("Bard") ? "&7» &cBard" : "&7Bard");
            lore.add(pvpClass.equals("Archer") ? "&7» &cArcher" : "&7Archer");
            lore.add(pvpClass.equals("Rogue") ? "&7» &cRogue" : "&7Rogue");
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(
                    pvpClass.equals("Diamond") ? Material.DIAMOND_CHESTPLATE :
                    pvpClass.equals("Bard") ? Material.GOLD_CHESTPLATE :
                    pvpClass.equals("Archer") ? Material.LEATHER_CHESTPLATE :
                    pvpClass.equals("Rogue") ? Material.IRON_CHESTPLATE : null)
                   .name("&a" + profile.getName())
                   .amount(1)
                   .lore(lore)
                   .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(uuid);
            Party party = Profile.getByPlayer(player).getParty();

            if (party != null) {
                if (party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                    Button.playSuccess(player);
                    String pvpClass = party.getKits().get(uuid);

                    if (pvpClass == null) {
                        player.sendMessage(CC.translate("&7An internal error occurred, please contact the author of this plugin!"));
                        player.closeInventory();
                        return;
                    }

                    switch (pvpClass) {
                        case "Diamond": {
                            party.getKits().replace(uuid, "Bard");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Bard").replace("<target>", profile.getName()));
                            break;
                        }
                        case "Bard": {
                            party.getKits().replace(uuid, "Archer");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Archer").replace("<target>", profile.getName()));
                            break;
                        }
                        case "Archer": {
                            party.getKits().replace(uuid, "Rogue");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Rogue").replace("<target>", profile.getName()));
                            break;
                        }
                        case "Rogue": {
                            party.getKits().replace(uuid, "Diamond");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Diamond").replace("<target>", profile.getName()));
                            break;
                        }
                    }
                } else {
                    Button.playFail(player);
                    player.sendMessage(Locale.PARTY_NOTLEADER.toString());
                }
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }
    }
}    