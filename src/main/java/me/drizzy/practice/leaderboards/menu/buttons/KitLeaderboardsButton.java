package me.drizzy.practice.leaderboards.menu.buttons;

import lombok.AllArgsConstructor;
import me.drizzy.practice.Locale;
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

import java.util.ArrayList;
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
        List<String> lore = new ArrayList<>();

        lore.add(CC.MENU_BAR);
        int position = 1;
        for (final LeaderboardsAdapter leaderboardsAdapter : this.kit.getRankedEloLeaderboards()) {
            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));
            lore.add(Locale.LEADERBOARDS_KIT_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(position))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", ChatColor.stripColor(profile.getEloLeague())));
            ++position;
        }
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(this.kit.getDisplayIcon()).name(Locale.LEADERBOARDS_KIT_HEADER.toString().replace("<kit_name>", this.kit.getDisplayName())).lore(lore).build();
    }
}

