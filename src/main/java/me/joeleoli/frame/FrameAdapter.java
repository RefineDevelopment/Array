package me.joeleoli.frame;

import java.util.List;
import org.bukkit.entity.Player;

public interface FrameAdapter {

	String getTitle(Player player);

	List<String> getLines(Player player);

}
