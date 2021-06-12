package xyz.refinedev.practice.clan.meta;

import lombok.*;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.ClanProfileType;

import java.util.UUID;


@Data
@AllArgsConstructor
public class ClanProfile {

    private UUID uuid;
    private Clan clan;
    private ClanProfileType clanProfileType;
}
