package me.drizzy.practice.robot;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.npc.CitizensNPC;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

public class RobotListener implements Listener {

    @EventHandler
    public void onSpawn(NPCSpawnEvent e) {
        Damageable damageable =(Damageable) e.getNPC().getEntity();
        if(damageable.getHealth() <= 0) {
            e.setCancelled(true);
        }
    }

}
