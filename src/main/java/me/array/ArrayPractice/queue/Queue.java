package me.array.ArrayPractice.queue;

import java.util.ArrayList;
import java.util.function.Predicate;

import me.array.ArrayPractice.kit.Kit;
import org.bukkit.Bukkit;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.UUID;
import java.util.List;

public class Queue
{
    private static List<Queue> queues;
    private final UUID uuid;
    private final Kit kit;
    private final QueueType type;
    private final LinkedList<QueueProfile> players;
    
    public Queue(final Kit kit, final QueueType type) {
        this.uuid = UUID.randomUUID();
        this.players = new LinkedList<>();
        this.kit = kit;
        this.type = type;
        Queue.queues.add(this);
    }
    
    public String getQueueName() {
        if (this.type == QueueType.RANKED) {
            return "Ranked " + this.kit.getName();
        }
        if (this.type == QueueType.UNRANKED) {
            return "Unranked " + this.kit.getName();
        }
        throw new AssertionError();
    }
    
    public void addPlayer(final Player player, final int elo) {
        final QueueProfile queueProfile = new QueueProfile(player.getUniqueId());
        queueProfile.setElo(elo);
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setQueue(this);
        profile.setQueueProfile(queueProfile);
        profile.setState(ProfileState.IN_QUEUE);
        profile.refreshHotbar();
        if (this.type == QueueType.UNRANKED) {
            player.sendMessage(CC.GRAY + "You have been added to the " + CC.AQUA + this.getQueueName() + CC.GRAY + " queue.");
        }
        if (this.type == QueueType.RANKED) {
            player.sendMessage(CC.GRAY + "You have been added to the " + CC.AQUA + this.getQueueName() + CC.GRAY +  " queue." + CC.AQUA + " [" + profile.getKitData().get(kit).getElo() + "]");
        }
        this.players.add(queueProfile);
    }
    
    public void removePlayer(final QueueProfile queueProfile) {
        this.players.remove(queueProfile);
        final Player player = Bukkit.getPlayer(queueProfile.getPlayerUuid());
        if (player != null && player.isOnline()) {
            player.sendMessage(CC.RED + "You have left the " + this.getQueueName() + " queue.");
        }
        final Profile profile = Profile.getByUuid(queueProfile.getPlayerUuid());
        profile.setQueue(null);
        profile.setQueueProfile(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
    }
    
    public static Queue getByUuid(final UUID uuid) {
        for (final Queue queue : Queue.queues) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }
        return null;
    }
    
    public static Queue getByPredicate(final Predicate<Queue> predicate) {
        for (final Queue queue : Queue.queues) {
            if (predicate.test(queue)) {
                return queue;
            }
        }
        return null;
    }
    
    public QueueType getQueueType() {
        return this.type;
    }
    
    public static List<Queue> getQueues() {
        return Queue.queues;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public QueueType getType() {
        return this.type;
    }
    
    public LinkedList<QueueProfile> getPlayers() {
        return this.players;
    }
    
    static {
        Queue.queues = new ArrayList<>();
    }
}
