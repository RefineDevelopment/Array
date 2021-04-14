package me.drizzy.practice.leaderboards.menu.buttons;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.other.SkullCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drizzy
 * Created at 4/13/2021
 */
public class StatsButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        lore.add(CC.MENU_BAR);
        for ( Kit kit : Kit.getKits() ) {
            if (kit.getGameRules().isRanked() && kit.isEnabled()) {
                lore.add("&b" + kit.getName() + ": &f" + profile.getStatisticsData().get(kit).getElo());
            }
        }
        lore.add(CC.MENU_BAR);
        lore.add("&aGlobal ELO: &f" + profile.getGlobalElo());
        lore.add("&aGlobal League: &f" + profile.getEloLeague());
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                .name("&b&l" + player.getName() + " | Statistics")
                .lore(lore)
                .build();
    }
}