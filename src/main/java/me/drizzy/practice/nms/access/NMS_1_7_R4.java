package me.drizzy.practice.nms.access;

import me.drizzy.practice.Array;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.util.FieldUtils;

import java.lang.reflect.Field;

public class NMS_1_7_R4 implements NMSAccess {

	@Override
	public int getPing(Player p) {
		CraftPlayer cp = (CraftPlayer) p;
		return cp.getHandle().ping;
	}

	@Override
	public Object getChannel(Player p) {
		Field f = FieldUtils.getField(NetworkManager.class, "m");
		CraftPlayer cp = (CraftPlayer) p;
		NetworkManager nm = cp.getHandle().playerConnection.networkManager;
		assert f != null;
		return FieldUtils.get(f, nm);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void breakBlockWithAnimation(Block b, int ticks) {
		if(ticks < 1) {
			for(int i = 0; i < 10; i++){
				((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(
						b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 120,
						((CraftWorld) b.getWorld()).getHandle().dimension, new PacketPlayOutBlockBreakAnimation(1, b.getX(), b.getY(), b.getZ(), i));
			}
			((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(
					b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 120,
					((CraftWorld) b.getWorld()).getHandle().dimension, new PacketPlayOutWorldEvent(2001, b.getX(), b.getY(), b.getZ(), b.getTypeId(), false));
			b.setType(Material.AIR);
		}
		else {
			new BukkitRunnable() {

				double counter = 0;
				final double toDo = ticks/10;

				@Override
				public void run() {
					for(int i = 0; i < toDo; i++) {
						((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(
								b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 120,
								((CraftWorld) b.getWorld()).getHandle().dimension, new PacketPlayOutBlockBreakAnimation(1, b.getX(), b.getY(), b.getZ(), (int) counter));
						counter += toDo;
					}
					if(counter >= 10) {
						((CraftServer) Bukkit.getServer()).getHandle().sendPacketNearby(
								b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 120,
								((CraftWorld) b.getWorld()).getHandle().dimension, new PacketPlayOutWorldEvent(2001, b.getX(), b.getY(), b.getZ(), b.getTypeId(), false));
						b.setType(Material.AIR);
						this.cancel();
					}
				}
			}.runTaskTimer(Array.getInstance(), 0, 1);
		}
	}

	@Override
	public int getVersion(Player p) {
		return ((CraftPlayer) p).getHandle().playerConnection.networkManager.getVersion();
	}

	@Override
	public void strikeLightning(Player p, Location loc) {
		PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(new EntityLightning(((CraftPlayer) p).getHandle().getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true, true));
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public String getLanguage(Player p) {
		return ((CraftPlayer) p).getHandle().locale;
	}
	
	@Override
	public void sendKnockback(Player p, double x, double y, double z){
		PacketPlayOutEntityVelocity packet = new PacketPlayOutEntityVelocity(p.getEntityId(), x, y, z);
		CraftPlayer nmsPlayer = (CraftPlayer)p;
		nmsPlayer.getHandle().playerConnection.sendPacket(packet);
	}
	
}
