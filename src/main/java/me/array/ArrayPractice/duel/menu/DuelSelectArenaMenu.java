package me.array.ArrayPractice.duel.menu;

import java.beans.ConstructorProperties;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.event.inventory.ClickType;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class DuelSelectArenaMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Select an arena";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (final Arena arena : Arena.getArenas()) {
            if (!arena.isSetup()) {
                continue;
            }
            if (!arena.getKits().contains(profile.getDuelProcedure().getKit().getName())) {
                continue;
            }
            if (profile.getDuelProcedure().getKit().getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                continue;
            }
            if (arena.getType() == ArenaType.DUPLICATE) {
                continue;
            }
            buttons.put(buttons.size(), new SelectArenaButton(arena));
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
    
    private class SelectArenaButton extends Button
    {
        private Arena arena;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(Material.PAPER).name("&b&l" + this.arena.getName()).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.getDuelProcedure().setArena(this.arena);
            profile.getDuelProcedure().send();
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
        }
        
        @ConstructorProperties({ "arena" })
        public SelectArenaButton(final Arena arena) {
            this.arena = arena;
        }
    }
}
