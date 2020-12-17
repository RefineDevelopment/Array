/*package me.array.ArrayPractice.hologram;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(label = { "hologram", "hologram create" }, permission = "practice.staff")
public class HologramCommand {
	public void execute(final Player player) {
		final List<String> lore = new ArrayList<>();
			if (player != null) {
				int added = 1;
				for (final KitLeaderboards kitLeaderboards : Profile.getGlobalEloLeaderboards()) {
					lore.add(CC.translate(" &f" + added + ". &b" + kitLeaderboards.getName() + ": &f" + kitLeaderboards.getElo()));
					++added;
				}
				for(String s : lore) {
					++added;
					new Holograms().createHologram(s, player);
			}
		}
	}
}*/