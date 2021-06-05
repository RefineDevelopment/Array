package me.drizzy.practice.util.tablist;

import lombok.Getter;
import me.drizzy.practice.util.tablist.adapter.TabAdapter;
import me.drizzy.practice.util.tablist.listener.TabListener;
import me.drizzy.practice.util.tablist.packet.TabPacket;
import me.drizzy.practice.util.tablist.runnable.TabRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class TablistHandler {

	@Getter
	private static TablistHandler instance;
	
	private final TabAdapter adapter;
	
	public TablistHandler(TabAdapter adapter, JavaPlugin plugin, long time) {
		instance = this;
		this.adapter = adapter;
		
		new TabPacket(plugin);
				
		Bukkit.getServer().getPluginManager().registerEvents(new TabListener(this), plugin);
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new TabRunnable(adapter), 60L, time);
	}
}