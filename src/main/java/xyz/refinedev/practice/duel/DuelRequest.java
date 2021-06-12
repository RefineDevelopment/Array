package xyz.refinedev.practice.duel;

import lombok.Data;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;

import java.util.UUID;

@Data
public class DuelRequest {

    private final UUID sender;
    private final boolean party;
    private Kit kit;
    private Arena arena;
    private final long timestamp = System.currentTimeMillis();

    public boolean isExpired() {
        return System.currentTimeMillis() - this.timestamp >= 30_000;
    }

}
