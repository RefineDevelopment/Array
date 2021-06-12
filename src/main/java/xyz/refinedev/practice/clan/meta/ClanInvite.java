package xyz.refinedev.practice.clan.meta;

import lombok.Getter;
import xyz.refinedev.practice.clan.Clan;

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
