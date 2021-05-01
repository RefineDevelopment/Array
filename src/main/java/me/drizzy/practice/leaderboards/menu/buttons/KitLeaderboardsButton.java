package me.drizzy.practice.leaderboards.menu.buttons;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.leaderboards.LeaderboardsAdapter;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Drizzy
 * Created at 4/13/2021
 */
@AllArgsConstructor
public class KitLeaderboardsButton extends Button {

    private final Kit kit;

    @Override
    public ItemStack getButtonItem(final Player player) {
        List<String> lore = Lists.newArrayList();
        lore.add(CC.MENU_BAR);
        int position = 1;
        for (final LeaderboardsAdapter leaderboardsAdapter : this.kit.getRankedEloLeaderboards()) {
            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));
            if (position == 1 || position == 2 || position == 3) {
                lore.add(" &a" + position + " &7&l| &c" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
            } else {
                lore.add(" &7" + position + " &7&l| &c" + leaderboardsAdapter.getName() + "&7: &f" + leaderboardsAdapter.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
            }
            ++position;
        }
        lore.add(CC.MENU_BAR);
        return new ItemBuilder(this.kit.getDisplayIcon()).name("&c" + this.kit.getName() + " &7&l| &fTop 10").lore(lore).build();
    }
}

