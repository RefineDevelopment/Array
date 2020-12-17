package me.joeleoli.frame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FrameListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Frame.getInstance().getBoards().put(event.getPlayer().getUniqueId(), new FrameBoard(event.getPlayer()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Frame.getInstance().getBoards().remove(event.getPlayer().getUniqueId());
	}

}
