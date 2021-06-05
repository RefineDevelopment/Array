package me.drizzy.practice.events.menu;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class EventSelectKitMenu extends Menu {

    private final String event;

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit to host " + event;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && !kit.getGameRules().isNoItems() && !kit.getGameRules().isLavaKill() && !kit.getGameRules().isWaterKill() && !kit.getGameRules().isSpleef() && !kit.getGameRules().isSumo()) {
                buttons.put(buttons.size(), new SelectKitButton(event, kit));
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectKitButton extends Button {

        private final String event;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&c" + kit.getName())
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (event.equals("Brackets")) {
                if (Array.getInstance().getBracketsManager().getActiveBrackets() != null) {
                    player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "Brackets").replace("<event_name>", "Brackets"));
                    return;
                }

                if (!Array.getInstance().getBracketsManager().getCooldown().hasExpired()) {
                    player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "Brackets").replace("<event_name>", "Brackets"));
                    return;
                }

                Array.getInstance().getBracketsManager().setActiveBrackets(new Brackets(player, kit));

                for (Player other : Array.getInstance().getServer().getOnlinePlayers()) {
                    Profile profile = Profile.getByUuid(other.getUniqueId());

                    if (profile.isInLobby()) {
                        if (!profile.getKitEditor().isActive()) {
                            PlayerUtil.reset(player);
                            profile.refreshHotbar();
                        }
                    }
                }
            } else {
                if (Array.getInstance().getLMSManager().getActiveLMS() != null) {
                    player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "LMS").replace("<event_name>", "LMS"));
                    return;
                }

                if (!Array.getInstance().getLMSManager().getCooldown().hasExpired()) {
                    player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "LMS").replace("<event_name>", "LMS"));
                    return;
                }

                Array.getInstance().getLMSManager().setActiveLMS(new LMS(player, kit));

                Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
            }
        }
    }

}
