package me.drizzy.practice.util.tablist.listener;

import lombok.AllArgsConstructor;
import me.drizzy.practice.util.tablist.TablistHandler;
import me.drizzy.practice.util.tablist.layout.TabLayout;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class TabListener implements Listener {

	private final TablistHandler instance;

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		TabLayout layout = new TabLayout(instance, player);
		layout.create();
		layout.setHeaderAndFooter();

		TabLayout.getLayoutMapping().put(player.getUniqueId(), layout);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		TabLayout.getLayoutMapping().remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		TabLayout.getLayoutMapping().remove(player.getUniqueId());
	}
}
