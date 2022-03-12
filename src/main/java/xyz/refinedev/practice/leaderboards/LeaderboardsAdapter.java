package xyz.refinedev.practice.leaderboards;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LeaderboardsAdapter {

    private String name;
    private UUID uniqueId;
    private int elo;

}
