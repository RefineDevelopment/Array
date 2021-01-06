package me.array.ArrayPractice.duel.menu;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.arena.ArenaType;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DuelSelectArenaMenu extends Menu {

    String type;

    public DuelSelectArenaMenu(String type) {
        this.type = type;
    }

    @Override
    public String getTitle(Player player) {
        return "&cSelect an arena";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        Map<Integer, Button> buttons = new HashMap<>();

        for (Arena arena : Arena.getArenas()) {
            if (!arena.isSetup()) {
                continue;
            }

            if (type.equalsIgnoreCase("normal")) {
                if (!arena.getKits().contains(profile.getDuelProcedure().getKit().getName())) {
                    continue;
                }

                if (profile.getDuelProcedure().getKit().getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                    continue;
                }

                if (arena.getType() == ArenaType.DUPLICATE) {
                    continue;
                }
            } else if (type.equalsIgnoreCase("rematch")) {
                if (!arena.getKits().contains(profile.getRematchData().getKit().getName())) {
                    continue;
                }

                if (profile.getRematchData().getKit().getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                    continue;
                }

                if (arena.getType() == ArenaType.DUPLICATE) {
                    continue;
                }
            }

            buttons.put(buttons.size(), new SelectArenaButton(arena));
        }

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!isClosedByMenu()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setDuelProcedure(null);
        }
    }

    @AllArgsConstructor
    private class SelectArenaButton extends Button {

        private final Arena arena;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name("&4" + arena.getName())
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (type.equalsIgnoreCase("normal")) {
                // Update and request the procedure
                profile.getDuelProcedure().setArena(arena);
                profile.getDuelProcedure().send();

                // Set closed by menu
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

                // Force close inventory
                player.closeInventory();
            } else if (type.equalsIgnoreCase("rematch")) {

                Profile targetProfile = Profile.getByUuid(profile.getRematchData().getTarget());
                // Update and request the procedure
                profile.getRematchData().setArena(arena);
                targetProfile.getRematchData().setArena(arena);
                profile.getRematchData().request();

                // Force close inventory
                player.closeInventory();
            }
        }

    }

}
