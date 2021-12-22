package xyz.refinedev.practice.queue;

import lombok.Getter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;

import java.util.LinkedList;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@Getter
public class Queue {

    private final Array plugin;
    private final LinkedList<QueueProfile> players = new LinkedList<>();

    private final UUID uniqueId;
    private final Kit kit;
    private final QueueType type;

    /**
     * Main constructor for {@link Queue}
     *
     * @param kit The kit of the queue
     * @param type The type of the queue
     */
    public Queue(Array plugin, Kit kit, QueueType type) {
        this.plugin = plugin;
        this.kit = kit;
        this.type = type;
        this.uniqueId = UUID.randomUUID();

        this.plugin.getQueueManager().getQueues().put(uniqueId, this);
    }

    /**
     * Get amount of players in fight from a certain
     * queue
     *
     * @return amount of players in fight with the queue
     */
    public int getInFights() {
        int i = 0;

        for ( Match match : this.plugin.getMatchManager().getMatches()) {
            if (match.getQueue() == null || !match.getQueue().equals(this)) continue;
            if (!match.isFighting() && !match.isStarting()) continue;

            i += match.getTeamPlayers().size();
        }
        return i;
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
}
