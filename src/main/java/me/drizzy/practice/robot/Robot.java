package me.drizzy.practice.robot;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

@Getter @Setter
public class Robot {

    /**
     * Note to Source Code Viewers
     *
     * This robot code is fully done by Drizzy, It might look like its "skidded" StrikePractice
     * But that's far from the truth, This took me 4 weeks to complete and is fully done by
     * Drizzy#0278 at Discord or https://discord.link/purge at Purge Community
     */

    private final NPC npc;
    private final RobotType type;

    private Kit kit;
    private Match match;
    private Location spawnLocation;
    private RobotLogic logic;

    private boolean deSpawned;

    public Robot(NPC npc, RobotType type) {
        this.npc = npc;
        this.type = type;

        deSpawned = false;
    }

    public Player getPlayer() {
        return (Player) npc.getEntity();
    }

    public void setSkin(String name) {
        npc.data().set("player-skin-name", name);
    }

    public void stop() {
        getPlayer().setHealthScale(20.0D);
        npc.despawn();
        npc.destroy();

        deSpawned = true;
    }

    public void hurt(boolean burn, boolean critical, boolean sharp) {
        this.getPlayer().playEffect(EntityEffect.HURT);

        for ( Entity ent : this.getPlayer().getNearbyEntities(100.0D, 100.0D, 100.0D))
            if (ent instanceof Player) {
                this.getPlayer().getWorld().playSound(this.getPlayer().getLocation(), Sound.HURT_FLESH, 0.7F, 1.0F);
            }

        if (burn) {
            this.getPlayer().setFireTicks(20);
        } else {
            Location l = this.getPlayer().getLocation().add(0.0D, 1.0D, 0.0D);
            int i;
            if (critical) {
                for(i = 0; i < Array.random.nextInt(5) + 10; ++i) {
                    l.getWorld().playEffect(l, Effect.CRIT, 1);
                }
            }

            if (sharp) {
                for(i = 0; i < Array.random.nextInt(5) + 10; ++i) {
                    l.getWorld().playEffect(l, Effect.MAGIC_CRIT, 1);
                }
            }
        }

    }

    public boolean isSpawned() {
        return this.npc.isSpawned();
    }

    public void swing() {
        if (getPlayer() != null) {
            PlayerAnimation.ARM_SWING.play(getPlayer());
        }
    }

    public void init(List<UUID> players) {
        this.logic = new RobotLogic(this, players, type);
    }

}
