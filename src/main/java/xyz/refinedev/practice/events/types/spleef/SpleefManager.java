package xyz.refinedev.practice.events.types.spleef;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.events.types.spleef.task.SpleefStartTask;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter @Setter
public class SpleefManager {

	private Spleef activeSpleef;
	private Cooldown cooldown = new Cooldown(0);
	private Location spleefSpawn;
	private String spleefKnockbackProfile;

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
			spleefSpawn= LocationUtil.deserialize(configuration.getString("events.spleef.spectator"));
		}

		if (configuration.contains("events.spleef.knockback-profile")) {
			spleefKnockbackProfile = configuration.getString("events.spleef.knockback-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (spleefSpawn != null) {
			configuration.set("events.spleef.spectator", LocationUtil.serialize(spleefSpawn));
		}

		if (spleefKnockbackProfile != null) {
			configuration.set("events.spleef.knockback-profile", spleefKnockbackProfile);
		}

		try {
			configuration.save(Array.getInstance().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
