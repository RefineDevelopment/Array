package xyz.refinedev.practice.queue.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.Clan;
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

public class QueueKitButton extends Button {

    private final Queue queue;

    public QueueKitButton(Array plugin, Queue queue) {
        super(plugin);

        this.queue = queue;
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = this.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        this.getLore().forEach(lines -> lore.add(CC.translate(this.replace(profile, lines))));

        return new ItemBuilder(queue.getKit().getDisplayIcon())
                .name(queue.getKit().getDisplayName())
                .lore(lore)
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();

        Profile profile = this.getPlugin().getProfileManager().getProfile(player.getUniqueId());
        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        switch (queue.getType()) {
            case UNRANKED: {
                this.getPlugin().getQueueManager().addPlayer(queue, player, 0);
                break;
            }
            case RANKED: {
                ProfileStatistics stats = profile.getStatisticsData().get(this.queue.getKit());
                this.getPlugin().getQueueManager().addPlayer(queue, player, stats.getElo());
                break;
            }
            case CLAN: {
                Clan clan = this.getPlugin().getClanManager().getByUUID(profile.getClan());
                this.getPlugin().getQueueManager().addPlayer(queue, player, clan.getElo());
                break;
            }
        }
    }

    public String replace(Profile profile, String input) {
        ProfileStatistics stats = profile.getStatisticsData().get(this.queue.getKit());
        Clan clan = this.getPlugin().getClanManager().getByUUID(profile.getClan());

        input = input
                .replace("<in_queue>", String.valueOf(this.queue.getPlayers().size()))
                .replace("<queue_elo>", String.valueOf(queue.getType() == QueueType.RANKED ? stats.getElo() : queue.getType() == QueueType.CLAN ? clan.getElo() : 0))
                .replace("<in_fight>", String.valueOf(this.queue.getInFights()));
        return input;
    }

    public List<String> getLore() {
        List<String> lore = new ArrayList<>();

        for ( String line : this.getPlugin().getConfigHandler().getQUEUE_LORE()) {
            if (line.contains("<description>")) {
                line = line.replace("<description>", "");
                lore.addAll(this.queue.getKit().getKitDescription().stream().map(CC::translate).collect(Collectors.toList()));
            }
            lore.add(CC.translate(line));
        }
        return lore;
    }
}
