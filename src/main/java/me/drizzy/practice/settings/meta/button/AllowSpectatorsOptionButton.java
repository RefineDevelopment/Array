package me.drizzy.practice.settings.meta.button;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.profile.option.menu.ProfileOptionButton;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class AllowSpectatorsOptionButton extends ProfileOptionButton
{
    @Override
    public ItemStack getEnabledItem(final Player player) {
        return new ItemBuilder(Material.REDSTONE_TORCH_ON).build();
    }
    
    @Override
    public ItemStack getDisabledItem(final Player player) {
        return new ItemBuilder(Material.REDSTONE_TORCH_ON).build();
    }
    
    @Override
    public String getOptionName() {
        return "&b&lSpectators";
    }
    
    @Override
    public String getDescription() {
        return "If enabled, players will be able to spectate your match.";
    }
    
    @Override
    public String getEnabledOption() {
        return "Allow players to spectate";
    }
    
    @Override
    public String getDisabledOption() {
        return "Do not allow players to spectate";
    }
    
    @Override
    public boolean isEnabled(final Player player) {
        return Profile.getByUuid(player.getUniqueId()).getSettings().isAllowSpectators();
    }
    
    @Override
    public void clicked(final Player player, final ClickType clickType) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getSettings().setAllowSpectators(!profile.getSettings().isAllowSpectators());
    }
}
