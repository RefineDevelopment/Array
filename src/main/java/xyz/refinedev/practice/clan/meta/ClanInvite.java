package xyz.refinedev.practice.clan.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ClanInvite {

    private final UUID player;

}
