package me.drizzy.practice.robot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.DifficultyType;
import me.drizzy.practice.kit.Kit;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author Drizzy
 * Created at 5/3/2021
 */

@Getter
@Setter
@RequiredArgsConstructor
public class Robot {

    private final Random random = new Random();

    private final NPC npc;
    private final DifficultyType botDifficulty;

    private Kit kit;
    private Arena arena;
    private RobotLogic logic;
    private boolean destroyed;

    public boolean isSpawned() {
        return this.npc.isSpawned();
    }

    public Player getPlayer() {
        return (Player)this.npc.getEntity();
    }

    /**
     * Make the Robot Swing his arm
     */
    public void swing() {
        if (this.getPlayer() != null) {
            PlayerAnimation.ARM_SWING.play(this.getPlayer());
        }
    }

    /**
     * Destroy and clear the Robot
     */
    public void destroy() {
        if (this.getPlayer() != null) {
            this.getPlayer().setHealth(20.0);
        }
        this.npc.despawn();
        this.npc.destroy();
        this.destroyed = true;
    }

    /**
     *
     * Play a hurt animation for the Robot
     *
     * @param burn If the hurt animation is from burning
     * @param critical If the hurt animation is from ciritical hits
     * @param sharp If the hurt animation is from sharpness hits
     */
    public void hurt(boolean burn, boolean critical, boolean sharp) {
        this.getPlayer().playEffect(EntityEffect.HURT);

        for (final Entity entity : this.getPlayer().getNearbyEntities(100.0, 100.0, 100.0)) {
            if (entity instanceof Player) {
                this.getPlayer().getWorld().playSound(this.getPlayer().getLocation(), Sound.HURT_FLESH, 0.7f, 1.0f);
            }
        }

        if (burn) {
            this.getPlayer().setFireTicks(20);
        } else {
            final Location location = this.getPlayer().getLocation().add(0.0, 1.0, 0.0);
            if (critical) {
                for (int i = 0; i < this.random.nextInt(5) + 10; ++i) {
                    location.getWorld().playEffect(location, Effect.CRIT, 1);
                }
            }
            if (sharp) {
                for (int i = 0; i < this.random.nextInt(5) + 10; ++i) {
                    location.getWorld().playEffect(location, Effect.MAGIC_CRIT, 1);
                }
            }
        }
    }

    /**
     * Set the bot's velocity
     *
     * @param vector {@link Vector}
     */
    public void setVelocity(Vector vector) {
        if(npc != null && !destroyed) {
            getPlayer().setVelocity(vector);
        }
    }

    /**
     * Initiate the Logic Runnable for the Bot
     * 
     * @param players The players which the robot will be engaging with
     * @param difficulty The {@link DifficultyType} of the robot
     */
    public void startLogic(List<UUID> players, DifficultyType difficulty) {
        this.logic = new RobotLogic(this, players, difficulty);
    }
}
