package me.drizzy.practice.kit;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class KitGameRules {

    private boolean ranked, boxuhc, infinitespeed, infinitestrength, partyffa, partysplit, editable, antifoodloss,
                    noitems, /*bridge,*/ build, spleef, sumo, combo, stickspawn, parkour, timed, waterkill, lavakill,
                    bowhp, voidspawn, healthRegeneration, showHealth, disablefalldamage = false;
    private int hitDelay = 20;
}
