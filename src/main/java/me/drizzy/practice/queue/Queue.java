package me.drizzy.practice.queue;

import lombok.Getter;
import me.drizzy.practice.Locale;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.other.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Queue {

    @Getter private static final List<Queue> queues = new ArrayList<>();
    @Getter private static final Map<Kit, Queue> queueMap = new HashMap<>();

    private final Map<UUID, Long> playerQueueTime = new HashMap<>();
    private final LinkedList<QueueProfile> players = new LinkedList<>();

    private final UUID uuid;
    private final Kit kit;
    private final QueueType type;

    /**
     * Main constructor for {@link Queue}
     *
     * @param kit The kit of the queue
     * @param type The type of the queue
     */
    public Queue(Kit kit, QueueType type) {
        this.kit = kit;
        this.type = type;
        this.uuid = UUID.randomUUID();
        queues.add(this);
        queueMap.put(kit, this);
    }

    public static void preLoad() {
        new QueueThread().start();
    }

    /**
     * Returns a {@link Queue} using its {@link UUID}
     *
     * @param uuid The UUID of the Queue
     * @return {@link Queue}
     */
    public static Queue getByUuid(UUID uuid) {
        for (Queue queue : queues) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }
        return null;
    }

    /**
     * Returns the Queue's Formatted Name
     *
     * @return Formatted Name in {@link String}
     */
    public String getQueueName() {
        switch (type) {
            case RANKED: return "Ranked " + kit.getDisplayName();
            case UNRANKED: return "Unranked " + kit.getDisplayName();
            case CLAN: return "Clan " + kit.getDisplayName();
            default: return kit.getDisplayName() + " Queue";
        }
    }

    /**
     * Get Queued Duration of the {@link Player}
     *
     * @param player The player whose Duration is being returned
     * @return Duration in {@link String} form
     */
    public String getDuration(Player player) {
        return TimeUtil.millisToTimer(System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId()));
    }

    /**
     * Add a player to a certain queue
     *
     * @param player The player being added to the queue
     * @param elo The ELO of the player
     */
    public void addPlayer(Player player, int elo) {
        QueueProfile queueProfile = new QueueProfile(player.getUniqueId());
        queueProfile.setElo(elo);

        this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setQueue(this);
        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.IN_QUEUE);
        profile.refreshHotbar();

        switch (type) {
            case UNRANKED: {
                player.sendMessage(Locale.QUEUE_JOIN_UNRANKED.toString().replace("<queue_name>", getQueueName()));
                break;
            }
            case RANKED: {
                player.sendMessage(Locale.QUEUE_JOIN_RANKED.toString()
                        .replace("<queue_name>", getQueueName())
                        .replace("<queue_elo>", String.valueOf(profile.getStatisticsData().get(kit).getElo())));
                break;
            }
            case CLAN: {
                player.sendMessage(Locale.QUEUE_JOIN_CLAN.toString()
                        .replace("<queue_name>", getQueueName())
                        .replace("<clan_elo>", String.valueOf(profile.getClan().getElo())));
                break;
            }
        }

        this.players.add(queueProfile);
    }

    /**
     * Remove a Player from Queue
     *
     * @param queueProfile The {@link QueueProfile} of the Player
     */
    public void removePlayer(QueueProfile queueProfile) {
        players.remove(queueProfile);

        Player player = Bukkit.getPlayer(queueProfile.getUuid());

        if (player != null && player.isOnline()) {
            player.sendMessage(Locale.QUEUE_LEAVE.toString().replace("<queue_name>", this.getQueueName()));
        }

        Profile profile = Profile.getByUuid(queueProfile.getUuid());
        profile.setQueue(null);
        profile.setQueueProfile(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
    }
}
