package me.drizzy.practice.clan;

import lombok.*;
import me.drizzy.practice.enums.ClanProfileType;

import java.util.UUID;


@Data
@AllArgsConstructor
public class ClanProfile {

    private UUID uuid;
    private Clan clan;
    private ClanProfileType clanProfileType;
}
