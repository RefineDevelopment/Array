package me.array.ArrayPractice.event.impl.spleef;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.event.impl.spleef.task.SpleefStartTask;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class SpleefManager {

	@Getter private Spleef activeSpleef;
	@Getter @Setter private Cooldown cooldown = new Cooldown(0);
	@Getter @Setter private Location spleefSpectator;
	@Getter @Setter private String spleefKnockbackProfile;

	public SpleefManager() {
		load();
	}

	public void setActiveSpleef(Spleef spleef) {
		if (activeSpleef != null) {
			activeSpleef.setEventTask(null);
		}

		if (spleef == null) {
			activeSpleef = null;
			return;
		}

		activeSpleef = spleef;
		activeSpleef.setEventTask(new SpleefStartTask(spleef));
	}

	public void load() {
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

		if (configuration.contains("events.spleef.spectator")) {
			spleefSpectator = LocationUtil.deserialize(configuration.getString("events.spleef.spectator"));
		}

		if (configuration.contains("events.spleef.knockback-profile")) {
			spleefKnockbackProfile = configuration.getString("events.spleef.knockback-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

		if (spleefSpectator != null) {
			configuration.set("events.spleef.spectator", LocationUtil.serialize(spleefSpectator));
		}

		if (spleefKnockbackProfile != null) {
			configuration.set("events.spleef.knockback-profile", spleefKnockbackProfile);
		}

		try {
			configuration.save(Array.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
