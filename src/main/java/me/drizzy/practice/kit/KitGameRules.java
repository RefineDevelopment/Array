package me.drizzy.practice.kit;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class KitGameRules {

    private boolean ranked, boxUHC, infiniteSpeed, infiniteStrength, disablePartyFFA, disablePartySplit, editable,
                    noItems, bridge, build, spleef, sumo, combo, stickSpawn, parkour, timed, waterKill, lavaKill,
                    bowHP, voidSpawn, healthRegeneration, showHealth, antiFoodLoss, mlgRush, clan, disableFallDamage = false;
    private int hitDelay = 20;
}
