

package me.array.ArrayPractice.profile.meta.option.button;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.profile.option.menu.ProfileOptionButton;
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
        return Profile.getProfiles().get(player.getUniqueId()).getOptions().isShowScoreboard();
    }
    
    @Override
    public void clicked(final Player player, final ClickType clickType) {
        final Profile profile = Profile.getProfiles().get(player.getUniqueId());
        profile.getOptions().setShowScoreboard(!profile.getOptions().isShowScoreboard());
    }
}
