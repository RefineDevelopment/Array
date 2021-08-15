package me.drizzy.practice.queue;

import lombok.Getter;
import me.drizzy.practice.Locale;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Queue {

    @Getter private static final List<Queue> queues = new ArrayList<>();
    @Getter private static final Map<Kit, Queue> queueMap = new HashMap<>();
    private final Map<UUID, Long> playerQueueTime = new HashMap<>();
    private final UUID uuid;
    private final Kit kit;
    private final QueueType type;
    private final LinkedList<QueueProfile> players = new LinkedList<>();

    public Queue(Kit kit, QueueType type) {
        this.kit = kit;
        this.type = type;
        this.uuid = UUID.randomUUID();
        queues.add(this);
        queueMap.put(kit, this);
    }

    public static Queue getByUuid(UUID uuid) {
        for (Queue queue : queues) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }
        return null;
    }

    public static Queue getByKit(Kit kit) {
        if (queueMap.containsKey(kit)) {
            return queueMap.get(kit);
        }
        return null;
    }

    public String getQueueName() {
        if (type == QueueType.RANKED) {
            return "Ranked " + kit.getName();
        } else if (type == QueueType.UNRANKED) {
            return "Unranked " + kit.getName();
        } else {
            throw new AssertionError();
        }
    }

    public Queue getRankedType() {
        if (type != QueueType.RANKED) {
            for ( Queue queue : queues ) {
                if (queue.getKit() == kit) {
                    if (queue.getType() != type) {
                        return queue;
                    }
                }
            }
        }
        return null;
    }

    public String getDuration(Player player) {
        return TimeUtil.millisToTimer(System.currentTimeMillis() - this.getPlayerQueueTime(player.getUniqueId()));
    }

    public void addPlayer(Player player, int elo) {
        QueueProfile queueProfile = new QueueProfile(player.getUniqueId());
        queueProfile.setElo(elo);
        this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setQueue(this);
        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.IN_QUEUE);
        profile.refreshHotbar();

        if (this.type == QueueType.UNRANKED) {
            player.sendMessage(Locale.QUEUE_JOIN_UNRANKED.toString()
                    .replace("<queue_name>", getQueueName()));
        }
        if (this.type == QueueType.RANKED) {
            player.sendMessage(Locale.QUEUE_JOIN_RANKED.toString()
                    .replace("<queue_name>", getQueueName())
                    .replace("<queue_elo>", String.valueOf(profile.getStatisticsData().get(kit).getElo())));
        }
        this.players.add(queueProfile);
    }

    public void removePlayer(QueueProfile queueProfile) {
        players.remove(queueProfile);

        Player player = Bukkit.getPlayer(queueProfile.getPlayerUuid());

        if (player != null && player.isOnline()) {
            player.sendMessage(CC.RED + "You have left the " + this.getQueueName() + " queue.");
        }

        Profile profile = Profile.getByUuid(queueProfile.getPlayerUuid());
        profile.setQueue(null);
        profile.setQueueProfile(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
    }

    public long getPlayerQueueTime(UUID uuid) {
        return this.playerQueueTime.get(uuid);
    }
}
