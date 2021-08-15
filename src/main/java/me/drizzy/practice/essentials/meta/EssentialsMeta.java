package me.drizzy.practice.essentials.meta;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Drizzy
 * Created at 4/16/2021
 */
@Getter
@Setter
public class EssentialsMeta {

    public boolean HCFEnabled = true;
    public boolean tabEnabled = true;
    public boolean coreHookEnabled = true;
    public boolean motdEnabled = true;
    public boolean disclaimerEnabled = true;
    public boolean removeBottles = true;
    public boolean rankedEnabled = true;
    public boolean requireKills = true;
    public boolean limitPing = true;
    public boolean bridgeClearBlocks = true;

    public int pingLimit = 300;
    public int requiredKills = 10;
    public int ffaSpawnRadius = 7;
    public int voidSpawnLevel = 45;
}
