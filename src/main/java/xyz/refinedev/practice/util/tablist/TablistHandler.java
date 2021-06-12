package xyz.refinedev.practice.util.tablist;

import lombok.Getter;
import xyz.refinedev.practice.util.tablist.adapter.TabAdapter;
import xyz.refinedev.practice.util.tablist.listener.TabListener;
import xyz.refinedev.practice.util.tablist.packet.TabPacket;
import xyz.refinedev.practice.util.tablist.runnable.TabRunnable;
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