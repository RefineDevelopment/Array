package xyz.refinedev.practice.party.menu;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/27/2021
 * Project: Array
 */

public class PartyClassSelectMenu extends PaginatedMenu {

    public PartyClassSelectMenu() {
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Select Armor Class";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        for (UUID uuid : new ArrayList<>(party.getKits().keySet())) {
            if (!(party.getPlayers().contains(uuid))) {
                party.getKits().remove(uuid);
            }
        }

        if (party != null && party.getPlayers() != null) {
            party.getPlayers().forEach(target -> buttons.put(buttons.size(), new MemberDisplayButton(target.getUniqueId())));
        }
        return buttons;
    }

    @AllArgsConstructor
    public static class MemberDisplayButton extends Button {

        private final UUID uuid;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            
            Player partyPlayer = Bukkit.getPlayer(uuid);

            lore.add(CC.MENU_BAR);
            lore.add(getPlayerClass(partyPlayer).equals("Diamond") ? "&7» &cDiamond" : "&7Diamond");
            lore.add(getPlayerClass(partyPlayer).equals("Bard") ? "&7» &cBard" : "&7Bard");
            lore.add(getPlayerClass(partyPlayer).equals("Archer") ? "&7» &cArcher" : "&7Archer");
            lore.add(getPlayerClass(partyPlayer).equals("Rogue") ? "&7» &cRogue" : "&7Rogue");
            lore.add(CC.MENU_BAR);

            return new ItemBuilder(
                    getPlayerClass(partyPlayer.getPlayer()).equals("Diamond") ? Material.DIAMOND_CHESTPLATE :
                    getPlayerClass(partyPlayer).equals("Bard") ? Material.GOLD_CHESTPLATE :
                    getPlayerClass(partyPlayer).equals("Archer") ? Material.LEATHER_CHESTPLATE :
                    getPlayerClass(partyPlayer).equals("Rogue") ? Material.IRON_CHESTPLATE :
                    null)
                   .name("&a" + partyPlayer.getName())
                   .amount(1)
                   .lore(lore)
                   .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Party party = Profile.getByPlayer(player).getParty();

            if (party != null) {
                if (party.getLeader().getUuid().equals(player.getUniqueId())) {
                    Button.playSuccess(player);
                    String pvpClass = party.getKits().get(uuid);

                    if (pvpClass == null) {
                        Array.logger("&cParty Class is null!");
                        return;
                    }

                    switch (pvpClass) {
                        case "Diamond":
                        case "diamond": {
                            party.getKits().remove(uuid);
                            party.getKits().put(uuid, "Bard");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Bard"));
                            break;
                        }
                        case "Bard":
                        case "bard": {
                            party.getKits().remove(uuid);
                            party.getKits().put(uuid, "Archer");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Archer"));
                            break;
                        }
                        case "Archer":
                        case "archer": {
                            party.getKits().remove(uuid);
                            party.getKits().put(uuid, "Rogue");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Rogue"));
                            break;
                        }
                        case "Rogue":
                        case "rogue": {
                            party.getKits().remove(uuid);
                            party.getKits().put(uuid, "Diamond");
                            party.broadcast(Locale.PARTY_HCF_UPDATED.toString().replace("<class>", "Diamond"));
                            break;
                        }
                    }
                } else {
                    Button.playFail(player);
                    player.sendMessage(ChatColor.RED + "You are not the leader of your party.");
                }
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }
    }

    public static String getPlayerClass(Player player) {
        Party party = Profile.getByPlayer(player).getParty();

        if (party == null) {
            return "None";
        }

        String pvpclass = party.getKits().get(player.getUniqueId());

        if (pvpclass == null) {
            return "Diamond";
        }

        if (pvpclass.contains("Bard")) {
            return "Bard";
        }
        
        if (pvpclass.contains("Rogue")) {
            return "Rogue";
        }

        if (pvpclass.contains("Archer")) {
            return "Archer";
        }

        return "Diamond";
    }
}    