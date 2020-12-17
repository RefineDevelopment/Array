

package me.array.ArrayPractice.duel;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.arena.Arena;

import java.util.UUID;

public class DuelRequest
{
    private final UUID sender;
    private final boolean party;
    private Kit kit;
    private Arena arena;
    private long timestamp;
    
    DuelRequest(final UUID sender, final boolean party) {
        this.timestamp = System.currentTimeMillis();
        this.sender = sender;
        this.party = party;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - this.timestamp >= 30000L;
    }
    
    public UUID getSender() {
        return this.sender;
    }
    
    public boolean isParty() {
        return this.party;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public void setKit(final Kit kit) {
        this.kit = kit;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public void setArena(final Arena arena) {
        this.arena = arena;
    }
}
