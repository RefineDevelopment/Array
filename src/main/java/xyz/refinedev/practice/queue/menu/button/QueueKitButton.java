package xyz.refinedev.practice.queue.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.managers.ClanManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.managers.QueueManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.statistics.ProfileStatistics;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/25/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class QueueKitButton extends Button {

    private final Queue queue;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        this.getLore(plugin).forEach(lines -> lore.add(CC.translate(this.replace(plugin, profile, lines))));

        ItemBuilder itemBuilder = new ItemBuilder(queue.getKit().getDisplayIcon());
        itemBuilder.name(queue.getKit().getDisplayName());
        itemBuilder.lore(lore);
        itemBuilder.clearFlags();

        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param plugin {@link org.bukkit.plugin.Plugin} Array
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = plugin.getProfileManager();
        QueueManager queueManager = plugin.getQueueManager();
        ClanManager clanManager = plugin.getClanManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        player.closeInventory();
        switch (queue.getType()) {
            case UNRANKED: {
                queueManager.addPlayer(queue, player, 0);
                break;
            }
            case RANKED: {
                ProfileStatistics stats = profile.getStatisticsData().get(this.queue.getKit());
                queueManager.addPlayer(queue, player, stats.getElo());
                break;
            }
            case CLAN: {
                Clan clan = clanManager.getByUUID(profile.getClan());
                queueManager.addPlayer(queue, player, clan.getElo());
                break;
            }
        }
    }

    public String replace(Array plugin, Profile profile, String input) {
        ClanManager clanManager = plugin.getClanManager();
        ProfileStatistics stats = profile.getStatisticsData().get(this.queue.getKit());
        Clan clan = clanManager.getByUUID(profile.getClan());

        input = input
                .replace("<in_queue>", String.valueOf(this.queue.getPlayers().size()))
                .replace("<queue_elo>", String.valueOf(queue.getType() == QueueType.RANKED ? stats.getElo() : queue.getType() == QueueType.CLAN ? clan.getElo() : 0))
                .replace("<in_fight>", String.valueOf(this.queue.getInFights()));
        return input;
    }

    public List<String> getLore(Array plugin) {
        List<String> lore = new ArrayList<>();

        for ( String line : plugin.getConfigHandler().getQUEUE_LORE()) {
            if (line.contains("<description>")) {
                line = line.replace("<description>", "");
                lore.addAll(this.queue.getKit().getKitDescription().stream().map(CC::translate).collect(Collectors.toList()));
            }
            lore.add(CC.translate(line));
        }
        return lore;
    }
}
