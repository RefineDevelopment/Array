package me.array.ArrayPractice.profile.stats.menu;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.meta.ProfileMatchHistory;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RevertEloMenu extends Menu {

    private final ProfileMatchHistory profileMatchHistory;

    @Override
    public String getTitle(Player player) {
        return "&cRevert Elo";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new RevertEloButton());

        return buttons;
    }

    @AllArgsConstructor
    private class RevertEloButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m-------------------------------");
            lore.add("&7Are you sure you want to make the following changes:");
            lore.add("&c" + profileMatchHistory.getFighter().getTeamPlayer().getUsername() + ": &7-" + profileMatchHistory.getEloChangeWinner());
            lore.add("&c" + profileMatchHistory.getOpponent().getTeamPlayer().getUsername() + ": &7+" + profileMatchHistory.getEloChangeLoser());
            lore.add("&8&m-------------------------------");
            lore.add("&7Left-Click to confirm");
            lore.add("&7Right-Click to cancel");
            lore.add("&8&m-------------------------------");
            return new ItemBuilder(Material.PAPER)
                    .name("&4Revert Elo")
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            if (clickType.isLeftClick()) {
                Kit kit = Kit.getByName(profileMatchHistory.getKit());
                Profile winner = Profile.getByUuid(profileMatchHistory.getFighter().getTeamPlayer().getUuid());
                Profile loser = Profile.getByUuid(profileMatchHistory.getOpponent().getTeamPlayer().getUuid());
                winner.getKitData().get(kit).setElo(winner.getKitData().get(kit).getElo() - profileMatchHistory.getEloChangeWinner());
                loser.getKitData().get(kit).setElo(loser.getKitData().get(kit).getElo() + profileMatchHistory.getEloChangeLoser());
                winner.getMatchHistory().remove(profileMatchHistory);
                loser.getMatchHistory().remove(profileMatchHistory);
                winner.save();
                loser.save();
                player.sendMessage(CC.translate("&4You made the following changes:"));
                player.sendMessage(CC.translate("&c" + profileMatchHistory.getFighter().getTeamPlayer().getUsername() + ": &7-" + profileMatchHistory.getEloChangeWinner()));
                player.sendMessage(CC.translate("&c" + profileMatchHistory.getOpponent().getTeamPlayer().getUsername() + ": &7+" + profileMatchHistory.getEloChangeLoser()));

            }
        }

    }

}
