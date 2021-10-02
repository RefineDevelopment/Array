package xyz.refinedev.practice.match.types.kit;

import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/2/2021
 * Project: Array
 */

public class BattleRushMatch extends SoloMatch {

    //Basically TheBridge but with no armor, shears and wool (red or blue) and yknow portals
    //kills on scoreboard

    /**
     * Construct a BattleRush match with the specified details
     *
     * @param queue     {@link Queue} if match is started from queue, then we provide it
     * @param playerA   {@link TeamPlayer} first player of the message
     * @param playerB   {@link TeamPlayer} second player of the message
     * @param kit       {@link Kit} The kit that will be given to all players in the match
     * @param arena     {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public BattleRushMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, playerA, playerB, kit, arena, queueType);
    }
}
