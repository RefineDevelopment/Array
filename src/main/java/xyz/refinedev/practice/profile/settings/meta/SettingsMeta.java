package xyz.refinedev.practice.profile.settings.meta;

import lombok.Data;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/16/2021
 * Project: Array
 */

@Data
public class SettingsMeta {

    private boolean
            scoreboardEnabled = true, receiveDuelRequests = true, allowSpectators = true, deathLightning = true, usingPingFactor = false,
            pingScoreboard = true, durationScoreboard = true, allowTournamentMessages = true, vanillaTab = false, showPlayers = false, cpsScoreboard = false,
            showSpectator = true, partyChat = false, clanChat = false, preventSword = false;
}