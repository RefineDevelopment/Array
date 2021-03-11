package me.drizzy.practice.duel.menu;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuelSelectKitMenu extends Menu {

    String type;

    public DuelSelectKitMenu(String type) {
        this.type = type;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        boolean party = Profile.getByUuid(player.getUniqueId()).getParty() != null;

        if (Array.getInstance().getMainConfig().getBoolean("Array.HCF-Enabled")) {
            for ( Kit kit : Kit.getKits() ) {
                if (kit.isEnabled() || kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                    if (!(kit.getGameRules().isTimed() && party))
                        buttons.put(buttons.size(), new SelectKitButton(kit));
                }
            }
        } else {
            for ( Kit kit : Kit.getKits() ) {
                if (kit.isEnabled() && !kit.getName().equalsIgnoreCase("HCFTeamFight")) {
                    if (!(kit.getGameRules().isTimed() && party))
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
            lore.add("&bClick to send a duel with this kit.");
            return new ItemBuilder(kit.getDisplayIcon())
                    .name(kit.getDisplayName()).lore(lore)
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (type.equalsIgnoreCase("normal")) {

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
                    new DuelSelectArenaMenu("normal").openMenu(player);
                } else {
                    profile.getDuelProcedure().send();
                }

            } else if (type.equalsIgnoreCase("rematch")) {
                if (profile.getRematchData() == null) {
                    player.sendMessage(CC.RED + "Could not find rematch data.");
                    return;
                }

                Profile targetProfile = Profile.getByUuid(profile.getRematchData().getTarget());

                Arena arena = Arena.getRandom(kit);
                // Update rematch data
                profile.getRematchData().setKit(kit);
                profile.getRematchData().setArena(arena);
                targetProfile.getRematchData().setKit(kit);
                targetProfile.getRematchData().setArena(arena);

                // Force close inventory
                player.closeInventory();
                if (player.hasPermission("array.donator")) {
                    new DuelSelectArenaMenu("rematch").openMenu(player);
                } else {
                    profile.getDuelProcedure().send();
                }
            }
        }

    }

}
