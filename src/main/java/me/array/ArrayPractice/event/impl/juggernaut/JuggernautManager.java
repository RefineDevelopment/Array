package me.array.ArrayPractice.event.impl.juggernaut;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.event.impl.juggernaut.task.JuggernautStartTask;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class JuggernautManager {

	@Getter private Juggernaut activeJuggernaut;
	@Getter @Setter private Cooldown cooldown = new Cooldown(0);
	@Getter @Setter private Location juggernautSpectator;
	@Getter @Setter private String juggernautKnockbackProfile;

	public JuggernautManager() {
		load();
	}

	public void setActiveJuggernaut(Juggernaut juggernaut) {
		if (activeJuggernaut != null) {
			activeJuggernaut.setEventTask(null);
		}

		if (juggernaut == null) {
			activeJuggernaut = null;
			return;
		}

		activeJuggernaut = juggernaut;
		activeJuggernaut.setEventTask(new JuggernautStartTask(juggernaut));
	}

	public void load() {
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

		if (configuration.contains("events.juggernaut.spectator")) {
			juggernautSpectator = LocationUtil.deserialize(configuration.getString("events.juggernaut.spectator"));
		}

		if (configuration.contains("events.juggernaut.knockback-profile")) {
			juggernautKnockbackProfile = configuration.getString("events.juggernaut.knockback-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

		if (juggernautSpectator != null) {
			configuration.set("events.juggernaut.spectator", LocationUtil.serialize(juggernautSpectator));
		}

		if (juggernautKnockbackProfile != null) {
			configuration.set("events.juggernaut.knockback-profile", juggernautKnockbackProfile);
		}

		try {
			configuration.save(Array.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
