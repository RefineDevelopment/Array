package me.drizzy.practice.util;

import me.drizzy.practice.Array;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {


	public static void playSound(Player p, Location location, String sound, float i, float f) {
		playSound(p, location, sound, i, f, false);
	}

	public static void playSound(Player p, Location location, String sound, float i, float f, boolean place) {
		if(sound == null || sound.equalsIgnoreCase("false")) return;
		Array plugin = Array.getInstance();
		if(!plugin.getNms().getVersion().startsWith("v1_7_R")
				&& !plugin.getNms().getVersion().startsWith("v1_8_R")) {
			if(sound.equals("CHICKEN_EGG_POP")) sound = "ENTITY_CHICKEN_EGG";
			if(sound.equals("CHEST_OPEN")) sound = "BLOCK_CHEST_OPEN";
			if(sound.equals("NOTE_PLING")) sound = "BLOCK_NOTE_PLING";
			if(sound.equals("HURT_FLESH")) sound = "ENTITY_PLAYER_HURT";
			if(sound.equals("AMBIENCE_THUNDER")) sound = "ENTITY_LIGHTNING_THUNDER";
			if(sound.equals("NOTE_PLING")) sound = "BLOCK_NOTE_PLING";
			if(sound.equals("LEVEL_UP")) sound = "ENTITY_PLAYER_LEVELUP";
			else if(place) {
				if(sound.equals("DIG_STONE")) sound = "BLOCK_STONE_PLACE";
				else if(sound.equals("DIG_WOOL")) sound = "BLOCK_STONE_PLACE";
				else if(sound.equals("DIG_GRASS")) sound = "BLOCK_GRASS_PLACE";
				else if(sound.equals("DIG_GRAVEL")) sound = "BLOCK_GRAVEL_PLACE";
				else if(sound.equals("DIG_SAND")) sound = "BLOCK_SAND_PLACE";
				else if(sound.equals("DIG_SNOW")) sound = "BLOCK_SNOW_PLACE";
				else if(sound.equals("DIG_WOOD")) sound = "BLOCK_WOOD_PLACE";
			}
		}
		Sound s = Sound.valueOf(sound);
		if(s == null) {
			Bukkit.getLogger().warning("Array >> Couldn't find sound '" + sound + "'");
		}
		else {
			p.playSound(location, s, i, f);
		}
	}
}
