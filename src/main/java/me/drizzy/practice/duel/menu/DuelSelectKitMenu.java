package me.drizzy.practice.duel.menu;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DuelSelectKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        boolean party = Profile.getByUuid(player.getUniqueId()).getParty() != null;

        if (party) {
            if (Essentials.getMeta().isHCFEnabled()) {
                for ( Kit kit : Kit.getKits() ) {
                    if (kit.isEnabled() || kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                        if (!kit.getGameRules().isTimed() && !kit.getGameRules().isBridge())
                            buttons.put(buttons.size(), new SelectKitButton(kit));
                    }
                }
            } else {
                for ( Kit kit : Kit.getKits() ) {
                    if (kit.isEnabled() && !kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                        if (!kit.getGameRules().isTimed() && !kit.getGameRules().isBridge())
                            buttons.put(buttons.size(), new SelectKitButton(kit));
                    }
                }
            }
        } else {
            for ( Kit kit : Kit.getKits() ) {
                if (kit.isEnabled() && !kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                    buttons.put(buttons.size(), new SelectKitButton(kit));
                }
            }
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
    private class SelectKitButton extends Button {

        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("&cClick to send a duel with this kit.");
            return new ItemBuilder(kit.getDisplayIcon())
                    .name(kit.getDisplayName()).lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());


                if (profile.getDuelProcedure() == null) {
                    player.sendMessage(CC.RED + "Could not find duel procedure.");
                    return;
                }

                Arena arena = Arena.getRandom(kit);
                // Update duel procedure
                profile.getDuelProcedure().setKit(kit);
                profile.getDuelProcedure().setArena(arena);

                // Set closed by menu
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

                // Force close inventory
                player.closeInventory();
                if (player.hasPermission("array.donator")) {
                    new DuelSelectArenaMenu().openMenu(player);
                } else {
                    profile.getDuelProcedure().send();
                }


        }

    }

}
