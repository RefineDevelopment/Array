

package me.array.ArrayPractice.duel.menu;

import java.beans.ConstructorProperties;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.event.inventory.ClickType;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class DuelSelectKitMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&bSelect a kit";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (final Kit kit : Kit.getKits()) {
            if (kit.isEnabled()) {
                buttons.put(buttons.size(), new SelectKitButton(kit));
            }
        }
        return buttons;
    }
    
    @Override
    public void onClose(final Player player) {
        if (!this.isClosedByMenu()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setDuelProcedure(null);
        }
    }
    
    private class SelectKitButton extends Button
    {
        private Kit kit;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&9&l" + this.kit.getName()).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getDuelProcedure() == null) {
                player.sendMessage(CC.RED + "Could not find duel procedure.");
                return;
            }
            final Arena arena = Arena.getRandom(this.kit);
            profile.getDuelProcedure().setKit(this.kit);
            profile.getDuelProcedure().setArena(arena);
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
            if (player.hasPermission("practice.selectarena")) {
                new DuelSelectArenaMenu().openMenu(player);
            }
            else {
                profile.getDuelProcedure().send();
            }
        }
        
        @ConstructorProperties({ "kit" })
        public SelectKitButton(final Kit kit) {
            this.kit = kit;
        }
    }
}
