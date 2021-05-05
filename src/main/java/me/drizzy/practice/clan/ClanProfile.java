package me.drizzy.practice.clan;

import lombok.*;

import java.util.UUID;


@Data
@AllArgsConstructor
public class ClanProfile {

    private UUID uuid;
    private Clan clan;
    private ClanProfileType clanProfileType;
}
