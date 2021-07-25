package xyz.refinedev.practice.essentials.meta;

import lombok.Data;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */

@Data
public class DefaultMeta {

    private boolean
            scoreboardEnabled = true, receiveDuelRequests = true, allowSpectators = true, deathLightning = true, pingFactor = false,
            pingScoreboard = true, durationScoreboard = true, tmessagesEnabled = true, vanillaTab = false, showPlayers = false, cpsScoreboard = false,
            showSpectator = true, preventSword = false;
}