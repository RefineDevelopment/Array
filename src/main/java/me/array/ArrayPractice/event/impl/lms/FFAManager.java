package me.array.ArrayPractice.event.impl.lms;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.event.impl.lms.task.FFAStartTask;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class FFAManager {

	@Getter private FFA activeFFA;
	@Getter @Setter private Cooldown cooldown = new Cooldown(0);
	@Getter @Setter private Location ffaSpectator;
	@Getter @Setter private String ffaKnockbackProfile;

	public FFAManager() {
		load();
	}

	public void setActiveFFA(FFA ffa) {
		if (activeFFA != null) {
			activeFFA.setEventTask(null);
		}

		if (ffa == null) {
			activeFFA = null;
			return;
		}

		activeFFA = ffa;
		activeFFA.setEventTask(new FFAStartTask(ffa));
	}

	public void load() {
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

		if (configuration.contains("events.ffa.spectator")) {
			ffaSpectator = LocationUtil.deserialize(configuration.getString("events.ffa.spectator"));
		}

		if (configuration.contains("events.ffa.knockback-profile")) {
			ffaKnockbackProfile = configuration.getString("events.ffa.knockback-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

		if (ffaSpectator != null) {
			configuration.set("events.ffa.spectator", LocationUtil.serialize(ffaSpectator));
		}

		if (ffaKnockbackProfile != null) {
			configuration.set("events.ffa.knockback-profile", ffaKnockbackProfile);
		}

		try {
			configuration.save(Array.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
