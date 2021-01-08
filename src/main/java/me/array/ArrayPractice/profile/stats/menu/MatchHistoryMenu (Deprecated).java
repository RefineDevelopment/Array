/*package me.array.ArrayPractice.profile.stats.menu;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.menu.MatchDetailsMenu;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.meta.ProfileMatchHistory;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MatchHistoryMenu extends Menu {

    private final Player target;
    private final boolean ranked;

    @Override
    public String getTitle(Player player) {
        return "&c" + target.getName() + "'s Match History";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Profile.getByUuid(target.getUniqueId());
        List<ProfileMatchHistory> profileMatchHistories = profile.getMatchHistory();
        profileMatchHistories.stream()
                .filter(profileMatchHistory -> profileMatchHistory.getMatchType().equalsIgnoreCase("ranked") == ranked)
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .forEach(profileMatchHistory -> buttons.put(buttons.size(), new MatchHistoryButton(profileMatchHistory, (buttons.size() + 1))));

        return buttons;
    }

    @AllArgsConstructor
    private class MatchHistoryButton extends Button {

        private final ProfileMatchHistory profileMatchHistory;
        private final Integer order;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m--------------------------");
            lore.add(" &cType: &r" + profileMatchHistory.getMatchType().toUpperCase());
            lore.add(" &cWinner: &r" + profileMatchHistory.getFighter().getTeamPlayer().getUsername()
                    + (profileMatchHistory.getMatchType().equalsIgnoreCase("ranked") ? (" (+" + profileMatchHistory.getEloChangeWinner() + " ELO)") : ""));
            lore.add(" &cLoser: &r" + profileMatchHistory.getOpponent().getTeamPlayer().getUsername()
                    + (profileMatchHistory.getMatchType().equalsIgnoreCase("ranked") ? (" (-" + profileMatchHistory.getEloChangeLoser() + " ELO)") : ""));
            lore.add(" &cDate: &r" + profileMatchHistory.getCreatedAt().toLocaleString());
            lore.add(" &cKit: &r" + profileMatchHistory.getKit());
            if(profileMatchHistory.getMatchType().equalsIgnoreCase("unranked") || profileMatchHistory.getMatchType().equalsIgnoreCase("ranked")) {
                if(profileMatchHistory.getKit().equalsIgnoreCase("sumo")) {
                    lore.add(" &cWinner Points: &r" + profileMatchHistory.getWinnerPoints());
                    lore.add(" &cLoser Points: &r" + profileMatchHistory.getLoserPoints());
                }
            }
            lore.add("&8&m--------------------------");
            lore.add("&7Left-Click to view the inventories");
            if (player.hasPermission("practice.staff") && profileMatchHistory.getMatchType().equalsIgnoreCase("ranked")) {
                lore.add("&7Right-Click to revert this elo change");
                lore.add("&cREAD ALL TEXT IN THE NEXT MENU CAREFULLY");
            }
            lore.add("&8&m--------------------------");

            return new ItemBuilder(Kit.getByName(profileMatchHistory.getKit()).getDisplayIcon())
                    .name((profileMatchHistory.isWon() ? "&a" : "&c") + "&lMatch #" + order + (profileMatchHistory.isWon() ? " (Won)" : " (Lost)"))
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            if (player.hasPermission("practice.staff") && profileMatchHistory.getMatchType().equalsIgnoreCase("ranked")) {
                if (clickType.isLeftClick())
                    new MatchDetailsMenu(profileMatchHistory.getFighter(), profileMatchHistory.getOpponent()).openMenu(player);
                else new RevertEloMenu(profileMatchHistory).openMenu(player);
            } else {
                new MatchDetailsMenu(profileMatchHistory.getFighter(), profileMatchHistory.getOpponent()).openMenu(player);
            }
        }

    }

}
*/