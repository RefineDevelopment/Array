package xyz.refinedev.practice.profile.settings.meta;

import lombok.Data;
import xyz.refinedev.practice.essentials.Essentials;

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

    scoreboardEnabled = Essentials.getDefaultMeta().isScoreboardEnabled(),
    receiveDuelRequests = Essentials.getDefaultMeta().isReceiveDuelRequests(),
    allowSpectators = Essentials.getDefaultMeta().isAllowSpectators(),

    //Donator
    deathLightning = true,
    pingFactor = false,
    rankedPingFactor,
    unrankedPingFactor,

    pingScoreboard = Essentials.getDefaultMeta().isPingScoreboard(),
    cpsScoreboard = Essentials.getDefaultMeta().isCpsScoreboard(),
    durationScoreboard = Essentials.getDefaultMeta().isDurationScoreboard(),
    tmessagesEnabled = Essentials.getDefaultMeta().isTmessagesEnabled(),
    vanillaTab = Essentials.getDefaultMeta().isVanillaTab(),
    showPlayers = Essentials.getDefaultMeta().isShowPlayers(),
    preventSword = Essentials.getDefaultMeta().isPreventSword(),

    //Automatic settings for Profile
    showSpectator = true,
    partyChat = false,
    clanChat = false;
}