package xyz.refinedev.practice.profile.settings.meta;

import lombok.Getter;
import lombok.Setter;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/16/2021
 * Project: Array
 */

@Getter @Setter
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

    //Visual Settings
    pingScoreboard = true,
    cpsScoreboard = false,
    durationScoreboard = true,
    tournamentMessages= true,
    vanillaTab = false,
    showPlayers = false,
    dropProtect = false,

    //Automatic settings for Profile
    showSpectator = true,
    partyChat = false,
    clanChat = false;
}