package me.drizzy.practice.clan.meta;

import lombok.*;
import me.drizzy.practice.clan.Clan;
import me.drizzy.practice.clan.ClanProfileType;

import java.util.UUID;


@Data
@AllArgsConstructor
public class ClanProfile {

    private UUID uuid;
    private Clan clan;
    private ClanProfileType clanProfileType;
}
