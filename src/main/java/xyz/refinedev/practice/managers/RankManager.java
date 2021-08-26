package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.rank.RankType;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/16/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class RankManager {

    private final Array plugin;
    private RankType rankType;

    public void init() {
        this.rankType = RankType.get();

        if (rankType.equals(RankType.DEFAULT)) {
            plugin.logger("&7No compatible Core was found, Defaulting to Green Color!");
            return;
        }
        plugin.logger("&aFound " + rankType.getName() + ", implementing core hook...");
    }


}
