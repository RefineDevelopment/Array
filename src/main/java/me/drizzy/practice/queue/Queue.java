package me.drizzy.practice.queue;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.PlayerUtil;

import java.util.*;
import java.util.function.Predicate;

public class Queue {

    @Getter
    private static final List<Queue> queues = new ArrayList<>();

    @Getter
    private static final Map<Kit, Queue> queuemap = new HashMap<>();

    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    private final Kit kit;
    @Getter
    private final QueueType type;
    @Getter
    private final LinkedList<QueueProfile> players = new LinkedList<>();

    public Queue(Kit kit, QueueType type) {
        this.kit = kit;
        this.type = type;

        queues.add(this);
        queuemap.put(kit, this);
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
        return queuemap.get(kit);
    }

    public static Queue getByPredicate(Predicate<Queue> predicate) {
        for (Queue queue : queues) {
            if (predicate.test(queue)) {
                return queue;
            }
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
            for (Queue queue : queues) {
                if (queue.getKit() == kit) {
                    if (queue.getQueueType() != type) {
                        return queue;
                    }
                }
            }
        }
        return null;
    }

    public void addPlayer(Player player, int elo) {
        QueueProfile queueProfile = new QueueProfile(player.getUniqueId());
        queueProfile.setElo(elo);

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setQueue(this);
        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.IN_QUEUE);
        PlayerUtil.reset(player, false);
        profile.refreshHotbar();

        if (this.type == QueueType.UNRANKED) {
            player.sendMessage(CC.GRAY + "You have been added to the " + CC.AQUA + this.getQueueName() + CC.GRAY + " queue.");
        }
        if (this.type == QueueType.RANKED) {
            player.sendMessage(CC.GRAY + "You have been added to the " + CC.AQUA + this.getQueueName() + CC.GRAY +  " queue." + CC.AQUA + " [" + profile.getKitData().get(kit).getElo() + "]");
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
        PlayerUtil.reset(profile.getPlayer(), false);
        profile.refreshHotbar();
    }

    public QueueType getQueueType() {
        return type;
    }
}
