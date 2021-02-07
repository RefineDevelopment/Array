package me.drizzy.practice.duel;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class DuelRequest {

    @Getter
    private final UUID sender;
    @Getter
    private final boolean party;
    @Getter
    @Setter
    private Kit kit;
    @Getter
    @Setter
    private Arena arena;
    private final long timestamp = System.currentTimeMillis();

    DuelRequest(UUID sender, boolean party) {
        this.sender = sender;
        this.party = party;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.timestamp >= 30_000;
    }

}
