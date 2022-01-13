package xyz.refinedev.practice.party;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PartyInvite {

    private final UUID uuid;
}
