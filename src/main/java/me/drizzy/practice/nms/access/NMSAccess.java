package me.drizzy.practice.nms.access;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
public interface NMSAccess {

	public int getPing(Player p);

	public Object getChannel(Player p);

	public void breakBlockWithAnimation(Block b, int ticks);

	public int getVersion(Player p);

	public void strikeLightning(Player p, Location loc);

	public String getLanguage(Player p);

	public void sendKnockback(Player p, double x, double y, double z);

}
