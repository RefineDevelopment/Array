package xyz.refinedev.practice.kit;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class KitGameRules {

    private boolean
            ranked,
            boxuhc,
            speed,
            strength,
            bedwars,
            noItems,
            bridge,
            build,
            spleef,
            sumo,
            combo,
            stickSpawn,
            parkour,
            timed,
            waterKill,
            lavaKill,
            boxing,
            wizard,
            bowHP,
            voidSpawn,
            regen,
            showHealth,
            antiFoodLoss,
            mlgRush,
            clan,

    //Other
    disableFallDamage = false, disablePartyFFA = false, disablePartySplit = false, editable = true;

    private int hitDelay = 20;
    private int bestOf = 1;
}
