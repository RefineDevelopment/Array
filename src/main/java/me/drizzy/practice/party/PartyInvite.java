package me.drizzy.practice.party;

import java.util.UUID;

public class PartyInvite
{
    private final UUID uuid;
    private final long expiresAt;
    
    public PartyInvite(final UUID uuid) {
        this.expiresAt = System.currentTimeMillis() + 30000L;
        this.uuid = uuid;
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expiresAt;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
}
