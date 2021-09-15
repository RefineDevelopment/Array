package xyz.refinedev.practice.match.types.kit;

import org.bukkit.entity.Player;
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

    public BoxingMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, playerA, playerB, kit, arena, queueType);
    }

    @Override
    public boolean canEnd() {
        return !this.getPlayerA().isAlive() || !this.getPlayerB().isAlive() || this.getPlayerA().isDisconnected() || this.getPlayerA().getHits() >= 100 || this.getPlayerB().isDisconnected() || this.getPlayerB().getHits() >= 100;
    }

    @Override
    public Player getWinningPlayer() {
        if (!this.getPlayerA().isAlive() || this.getPlayerA().isDisconnected() || this.getPlayerBRounds() >= 100) {
            return this.getPlayerB().getPlayer();
        }
        if (!this.getPlayerB().isAlive() || this.getPlayerB().isDisconnected() || this.getPlayerARounds() >= 100) {
            return this.getPlayerA().getPlayer();
        }
        return null;
    }
}
