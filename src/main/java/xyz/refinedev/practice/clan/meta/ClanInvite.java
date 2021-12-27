package xyz.refinedev.practice.clan.meta;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.clan.Clan;

import java.util.UUID;

@Getter
public class ClanInvite {

    private final Clan clan;
    private final UUID player;
    private final long expiresAt;

    public ClanInvite(UUID player, Clan clan) {
        this.expiresAt = System.currentTimeMillis() + 30000L;
        this.player = player;
        this.clan = clan;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expiresAt;
    }

}
