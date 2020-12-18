package me.array.ArrayPractice.profile.stats.menu;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class StatsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return ChatColor.AQUA + player.getName() + "'s &bStatistics";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons =new HashMap<>();
        buttons.put(0, new GlobalStatsButton());
        for (final Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                buttons.put(buttons.size(), new KitStatsButton(kit));
            }
        }
        return buttons;
    }
    
    private static class KitStatsButton extends Button
    {
        private final Kit kit;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore = new ArrayList<>();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            final String elo = this.kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(this.kit).getElo()) : "N/A";
            final String wins = this.kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(this.kit).getWon()) : "N/A";
            final String losses = this.kit.getGameRules().isRanked() ? Integer.toString(profile.getKitData().get(this.kit).getLost()) : "N/A";
            lore.add(" &b&lStats:");
            lore.add("  &fELO: &b" + elo);
            lore.add("  &fWins: &b" + wins);
            lore.add("  &fLosses: &b" + losses);
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&b&l" + this.kit.getName()).lore(lore).build();
        }
        
        @ConstructorProperties({ "kit" })
        public KitStatsButton(final Kit kit) {
            this.kit = kit;
        }
    }
    
    private static class GlobalStatsButton extends Button
    {
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore = new ArrayList<>();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            lore.add("  &fELO: &a" + profile.getGlobalElo());
            lore.add("  &fWins: &a" + profile.getGlobalWins());
            lore.add("  &fLosses: &a" + profile.getGlobalLosses());
            return new ItemBuilder(Material.QUARTZ).name("&b&lGlobal Stats").lore(lore).build();
        }


        public GlobalStatsButton() {
        }


    }
}
