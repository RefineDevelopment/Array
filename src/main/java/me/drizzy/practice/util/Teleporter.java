package me.drizzy.practice.util;

import me.drizzy.practice.Array;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

public class Teleporter {
	
	
	public static boolean teleport(Player p, Location to, boolean noDefaultWorld) {
		if(to.getWorld() == null || Bukkit.getWorld(to.getWorld().getName()) == null) return false;
		if(noDefaultWorld && to.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) return false;
		new BukkitRunnable() {
			
			@Override
			public void run() {
				execute(p, to);
			}
		}.runTask(Array.getInstance());
		return true;
	}
	
	public static void syncTeleport(Entity ent, Location to) {
		if(Bukkit.isPrimaryThread()) {
			ent.teleport(to);
			return;
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(ent != null) {
					ent.teleport(to);
				}
			}
		}.runTask(Array.getInstance());
	}
	
	private static void execute(Player p, Location to) {
		Block b = to.clone().subtract(0, 1, 0).getBlock();
		if(b.getType() != null && b.getType().isSolid()) {
			to.add(0, 1, 0);
		}
		for(int i = 0; i < 3; i++) {
			if(to.getBlock().getType().isSolid()) {
				to.add(0, 1, 0);
			}
		}
		if(p.teleport(to)) {
			Location l = p.getLocation().clone();
			Location l2 = to.clone();
			if(l.getWorld().getName().equals(l2.getWorld().getName())) {
				dist(l.getX(), l2.getX(), l.getZ(), l2.getZ());
			}
		}
	}
	
	private static double dist(double x1, double x2, double z1, double z2) {
		return NumberConversions.square(x1 - x2) + NumberConversions.square(z1 - z2);
	}
}
