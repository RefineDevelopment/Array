package me.blazingtide.pistol;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.blazingtide.pistol.adapter.PistolAdapter;
import me.blazingtide.pistol.board.PistolBoard;
import me.blazingtide.pistol.listener.PistolListener;
import me.blazingtide.pistol.thread.PistolThread;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

/**
 * Main handler class for library.
 * <p>
 * To begin do "new Pistol(javaPlugin, adapter);"
 */
@Getter
public class Pistol {

    private final JavaPlugin plugin;
    private final PistolAdapter adapter;
    
    private final Map<UUID, PistolBoard> boards = Maps.newHashMap();

    /**
     * @param plugin  the JavaPlugin that's using this API
     * @param adapter the adapter for reading the lines of a scoreboard
     */
    public Pistol(JavaPlugin plugin, PistolAdapter adapter) {
        this.plugin = plugin;
        this.adapter = adapter;

        Bukkit.getPluginManager().registerEvents(new PistolListener(this), plugin);
        new PistolThread(this).runTaskTimerAsynchronously(plugin, 2L, 2L);
    }
}
