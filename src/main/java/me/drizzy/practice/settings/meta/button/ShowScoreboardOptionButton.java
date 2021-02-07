

package me.drizzy.practice.settings.meta.button;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.profile.option.menu.ProfileOptionButton;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class ShowScoreboardOptionButton extends ProfileOptionButton
{
    @Override
    public String getOptionName() {
        return "&a&lShow Scoreboard";
    }
    
    @Override
    public ItemStack getEnabledItem(final Player player) {
        return new ItemBuilder(Material.ITEM_FRAME).build();
    }
    
    @Override
    public ItemStack getDisabledItem(final Player player) {
        return new ItemBuilder(Material.ITEM_FRAME).build();
    }
    
    @Override
    public String getDescription() {
        return "If enabled, a scoreboard will be displayed to you.";
    }
    
    @Override
    public String getEnabledOption() {
        return "Show you a scoreboard";
    }
    
    @Override
    public String getDisabledOption() {
        return "Do not show you a scoreboard";
    }
    
    @Override
    public boolean isEnabled(final Player player) {
        return Profile.getProfiles().get(player.getUniqueId()).getSettings().isShowScoreboard();
    }
    
    @Override
    public void clicked(final Player player, final ClickType clickType) {
        final Profile profile = Profile.getProfiles().get(player.getUniqueId());
        profile.getSettings().setShowScoreboard(!profile.getSettings().isShowScoreboard());
    }
}
