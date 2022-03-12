package xyz.refinedev.practice.match.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.menu.MatchDetailsMenu;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.SkullCreator;

import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/29/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SpectateButton extends Button {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuHandler().getConfigByName("general");

    private final Match match;
    private final TeamPlayer teamPlayer;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        String key = "SPECTATING_MENU.";

        ItemBuilder builder = new ItemBuilder(SkullCreator.itemFromUuid(teamPlayer.getUniqueId()));

        builder.name((teamPlayer.isAlive() ? config.getString(key + "ALIVE_NAME") :
                config.getString(key + "DEAD_NAME")).replace("<player_name>", teamPlayer.getUsername()));

        builder.lore(config.getStringList(key + "HEAD_LORE").stream()
                .map(s -> s.replace("<player_status>", teamPlayer.isAlive() ? "&aAlive" : "&cDead"))
                .collect(Collectors.toList()));

        builder.clearFlags();
        builder.clearEnchantments();

        return builder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        player.closeInventory();
        Button.playSuccess(player);

        switch (clickType) {
            case LEFT: {
                player.teleport(teamPlayer.getPlayer().getLocation());
                break;
            }
            case RIGHT: {
                MatchSnapshot matchSnapshot = match.getSnapshots().stream()
                        .filter(m -> m.getTeamPlayer().getUniqueId().equals(teamPlayer.getUniqueId()))
                        .findFirst()
                        .orElse(null);

                if (matchSnapshot == null) matchSnapshot = new MatchSnapshot(teamPlayer);

                MatchDetailsMenu menu = new MatchDetailsMenu(matchSnapshot, null);
                menu.openMenu(player);
                break;
            }
        }
    }
}
