package xyz.refinedev.practice.clan.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.ClanRoleType;

import java.util.UUID;


@Data
@AllArgsConstructor
public class ClanProfile {

    private UUID uuid;
    private Clan clan;
    private ClanRoleType type;
}
