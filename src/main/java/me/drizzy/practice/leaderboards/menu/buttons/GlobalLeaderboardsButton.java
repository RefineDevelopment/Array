package me.drizzy.practice.leaderboards.menu.buttons;

import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.leaderboards.LeaderboardsAdapter;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drizzy
 * Created at 4/13/2021
 */
public class GlobalLeaderboardsButton extends Button {

    @Override
    public ItemStack getButtonItem(final Player player) {
        final List<String> lore =new ArrayList<>();
        int position = 1;
        lore.add(CC.MENU_BAR);
        for (final LeaderboardsAdapter leaderboardsAdapter : Profile.getGlobalEloLeaderboards()) {
            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));
            if (position == 1 || position == 2 || position == 3) {
                lore.add(" &a" + position + " &7&l| &b" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
            } else {
                lore.add(" &7" + position + " &7&l| &b" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
            }
            ++position;
        }
        lore.add(CC.MENU_BAR);
        return new ItemBuilder(Material.SUGAR).name("&bGlobal &7| &fTop 10").lore(lore).build();
    }
}
