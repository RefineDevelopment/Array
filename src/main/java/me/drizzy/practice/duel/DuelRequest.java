package me.drizzy.practice.duel;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import lombok.Getter;
import lombok.Setter;

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
