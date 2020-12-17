package me.array.ArrayPractice.util.essentials.listener;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.bootstrap.BootstrappedListener;
import me.array.ArrayPractice.util.external.CC;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class EssentialsListener extends BootstrappedListener {

	private static List<String> BLOCKED_COMMANDS = Arrays.asList(
			"//calc",
			"//eval",
			"//solve",
			"/bukkit:",
			"/me",
			"/bukkit:me",
			"/minecraft:",
			"/minecraft:me"
	);

	public EssentialsListener(Array Array) {
		super(Array);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		if (BLOCKED_COMMANDS.contains(event.getMessage().toLowerCase())) {
			player.sendMessage(CC.RED + "You cannot perform this command.");
			event.setCancelled(true);
		}
	}

}
