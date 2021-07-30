package xyz.refinedev.practice.profile.settings.meta;

import lombok.Data;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/16/2021
 * Project: Array
 */

@Data
public class SettingsMeta {

    private boolean

    scoreboardEnabled = true,
    receiveDuelRequests = true,
    allowSpectators = true,

    //Donator
    deathLightning = true,
    pingFactor = false,
    rankedPingFactor,
    unrankedPingFactor,

    pingScoreboard = true,
    cpsScoreboard = false,
    durationScoreboard = true,
    tmessagesEnabled = true,
    vanillaTab = false,
    showPlayers = false,
    preventSword = false,

    //Automatic settings for Profile
    showSpectator = true,
    partyChat = false,
    clanChat = false;
}