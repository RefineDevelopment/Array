package me.drizzy.practice.kit;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class KitGameRules {

    private boolean ranked, boxUHC, infiniteSpeed, infiniteStrength, disablePartyFFA = false, disablePartySplit = false, editable = true, bedwars,
                    noItems, bridge, build, spleef, sumo, combo, stickSpawn, parkour, timed, waterKill, lavaKill, boxing, wizard,
                    bowHP, voidSpawn, healthRegeneration, showHealth, antiFoodLoss, mlgRush, clan, disableFallDamage = false;

    private int hitDelay = 20;
}
