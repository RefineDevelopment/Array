package me.drizzy.practice.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SoundUtil {

	public static void playPlaceSound(Player p, Location l, Material material) {
		String s = null;
		String m = material.toString();
		if(m.contains("STONE")) {
			s = "DIG_STONE";
		}
		else if(m.contains("WOOL")) {
			s = "DIG_WOOL";
		}
		else if(m.contains("GRASS") || m.contains("DIRT")) {
			s = "DIG_GRASS";
		}
		else if(m.contains("GRAVEL")) {
			s = "DIG_GRAVEL";
		}
		else if(m.contains("SAND")) {
			s = "DIG_SAND";
		}
		else if(m.contains("SNOW")) {
			s = "DIG_SNOW";
		}
		else if(m.contains("WOOD") || m.contains("LOG")) {
			s = "DIG_WOOD";
		}
		if(s != null) {
			SoundManager.playSound(p, l, s, 1, 1, true);
		}
	}

	public static void playStepSound(Player p, Location l, Material material) {
		String s = null;
		String m = material.toString();
		if(m.contains("STONE")) {
			s = "STEP_STONE";
		}
		else if(m.contains("WOOL")) {
			s = "STEP_WOOL";
		}
		else if(m.contains("GRASS") || m.contains("DIRT")) {
			s = "STEP_GRASS";
		}
		else if(m.contains("GRAVEL")) {
			s = "STEP_GRAVEL";
		}
		else if(m.contains("SAND")) {
			s = "STEP_SAND";
		}
		else if(m.contains("SNOW")) {
			s = "STEP_SNOW";
		}
		else if(m.contains("WOOD") || m.contains("LOG")) {
			s = "STEP_WOOD";
		}
		if(s != null) {
			SoundManager.playSound(p, l, s, 1, 1, true);
		}
	}

}
