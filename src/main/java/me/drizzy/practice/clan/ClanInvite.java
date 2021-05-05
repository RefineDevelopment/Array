package me.drizzy.practice.clan;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
public class ClanInvite {

    private final Clan clan;
    private final long expiresAt;

    public ClanInvite(final Clan clan) {
        this.expiresAt = System.currentTimeMillis() + 30000L;
        this.clan = clan;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expiresAt;
    }

}
