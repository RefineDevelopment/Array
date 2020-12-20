package me.array.ArrayPractice.profile.stats.menu;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.Material;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class RankedLeaderboardsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Leaderboards";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new GlobalLeaderboardsButton());
        for (final Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && kit.getGameRules().isRanked()) {
                buttons.put(buttons.size(), new KitLeaderboardsButton(kit));
            }
        }
        return buttons;
    }
    
    private static class KitLeaderboardsButton extends Button
    {
        private final Kit kit;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore = new ArrayList<>();
            int pos = 1;
            for (final KitLeaderboards kitLeaderboards : this.kit.getRankedEloLeaderboards()) {
                lore.add(" &b#" + pos + ". &7" + kitLeaderboards.getName() + ": &b" + kitLeaderboards.getElo() + " &7Elo");
                ++pos;
            }
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&b&l" + this.kit.getName()).lore(lore).build();
        }
        
        @ConstructorProperties({ "kit" })
        public KitLeaderboardsButton(final Kit kit) {
            this.kit = kit;
        }
    }
    
    private static class GlobalLeaderboardsButton extends Button
    {
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore =new ArrayList<>();
            int pos = 1;
            for (final KitLeaderboards kitLeaderboards : Profile.getGlobalEloLeaderboards()) {
                lore.add(" &b#" + pos + ". &7" + kitLeaderboards.getName() + ": &b" + kitLeaderboards.getElo() + " &7Elo");
                ++pos;
            }
            return new ItemBuilder(Material.TRIPWIRE_HOOK).name("&b&lGlobal").lore(lore).build();
        }
        
        public GlobalLeaderboardsButton() {
        }
    }
}
