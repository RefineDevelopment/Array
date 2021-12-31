package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueProfile;
import xyz.refinedev.practice.queue.QueueThread;

import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/17/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class QueueManager {

    private final Array plugin;
    private final Map<UUID, Queue> queues = new HashMap<>();

    private QueueThread thread;

    public void init() {
        this.thread = new QueueThread(plugin);
        this.thread.start();
    }

    /**
     * Shutdown the {@link QueueThread}
     */
    public void shutdown() {
        this.thread.stop();
        this.queues.clear();
    }

    /**
     * Add a player to a certain queue
     *
     * @param queue The queue being utilized
     * @param player The player being added to the queue
     * @param elo The ELO of the player
     */
    public void addPlayer(Queue queue, Player player, int elo) {
        QueueProfile queueProfile = new QueueProfile(player.getUniqueId());
        queueProfile.setElo(elo);

        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.setQueue(queue);
        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.IN_QUEUE);

        plugin.getProfileManager().refreshHotbar(profile);

        switch (queue.getType()) {
            case UNRANKED: {
                player.sendMessage(Locale.QUEUE_JOIN_UNRANKED.toString().replace("<queue_name>", queue.getQueueName()));
                break;
            }
            case RANKED: {
                player.sendMessage(Locale.QUEUE_JOIN_RANKED.toString()
                        .replace("<queue_name>", queue.getQueueName())
                        .replace("<queue_elo>", String.valueOf(profile.getStatisticsData().get(queue.getKit()).getElo())));
                break;
            }
            case CLAN: {
                Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());
                player.sendMessage(Locale.QUEUE_JOIN_CLAN.toString()
                        .replace("<queue_name>", queue.getQueueName())
                        .replace("<clan_elo>", String.valueOf(clan.getElo())));
                break;
            }
        }

        queue.getPlayers().add(queueProfile);
    }

    /**
     * Remove a Player from Queue
     *
     * @param queue The queue being utilized
     * @param queueProfile The {@link QueueProfile} of the Player
     */
    public void removePlayer(Queue queue, QueueProfile queueProfile) {
        queue.getPlayers().remove(queueProfile);

        Player player = Bukkit.getPlayer(queueProfile.getUniqueId());

        if (player != null && player.isOnline()) {
            player.sendMessage(Locale.QUEUE_LEAVE.toString().replace("<queue_name>", queue.getQueueName()));
        }

        Profile profile = plugin.getProfileManager().getByUUID(queueProfile.getUniqueId());
        profile.setQueue(null);
        profile.setQueueProfile(null);
        profile.setState(ProfileState.IN_LOBBY);

        plugin.getProfileManager().refreshHotbar(profile);
    }

    /**
     * Returns a {@link Queue} using its {@link UUID}
     *
     * @param uuid The UUID of the Queue
     * @return {@link Queue}
     */
    public Queue getByUuid(UUID uuid) {
        return queues.get(uuid);
    }
}
