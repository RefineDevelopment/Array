package me.drizzy.practice.events.types.spleef;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.LocationUtil;
import me.drizzy.practice.events.types.spleef.task.SpleefStartTask;
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
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (configuration.contains("events.spleef.spectator")) {
			spleefSpectator = LocationUtil.deserialize(configuration.getString("events.spleef.spectator"));
		}

		if (configuration.contains("events.spleef.nms-profile")) {
			spleefKnockbackProfile = configuration.getString("events.spleef.nms-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (spleefSpectator != null) {
			configuration.set("events.spleef.spectator", LocationUtil.serialize(spleefSpectator));
		}

		if (spleefKnockbackProfile != null) {
			configuration.set("events.spleef.nms-profile", spleefKnockbackProfile);
		}

		try {
			configuration.save(Array.getInstance().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
