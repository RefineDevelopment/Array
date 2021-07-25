package xyz.refinedev.practice.match.types.kit;

import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/7/2021
 * Project: Array
 */

public class BoxingMatch extends SoloMatch {

    private final TeamPlayer playerA;
    private final TeamPlayer playerB;

    private String eloMessage;
    private String specMessage;

    public BoxingMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, playerA, playerB, kit, arena, queueType);

        this.playerA = playerA;
        this.playerB = playerB;
    }
}
